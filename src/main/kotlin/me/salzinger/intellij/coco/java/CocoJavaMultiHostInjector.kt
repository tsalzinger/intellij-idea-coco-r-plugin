package me.salzinger.intellij.coco.java

import com.intellij.lang.Language
import com.intellij.lang.injection.MultiHostInjector
import com.intellij.lang.injection.MultiHostRegistrar
import com.intellij.lang.java.JavaLanguage
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiLanguageInjectionHost
import com.intellij.psi.util.PsiTreeUtil
import me.salzinger.intellij.coco.settings.CocoConfiguration
import me.salzinger.intellij.coco.settings.CocoInjectionMode
import java.util.regex.Pattern

class CocoJavaMultiHostInjector : MultiHostInjector {
    override fun getLanguagesToInject(registrar: MultiHostRegistrar, context: PsiElement) {
        if (CocoConfiguration.getSettings(context.project).injectionMode != CocoInjectionMode.ADVANCED) return
        if (context !is me.salzinger.intellij.coco.psi.CocoCocoInjectorHost) return

        val registrarAdapter = LazyMultiHostRegistrarAdapter(registrar, context)
        performInjection(registrarAdapter, context)
    }

    private fun performInjection(
        registrar: LazyMultiHostRegistrarAdapter,
        context: me.salzinger.intellij.coco.psi.CocoCocoInjectorHost
    ) {

        val psiFile = context.containingFile
        var prefixBuilder = StringBuilder()
        val targetPackage = CocoJavaUtil.getTargetPackage(psiFile).map { "package $it;\n\n" }.orElse("")
        prefixBuilder.append(targetPackage)

        registrar.startInjecting(JavaLanguage.INSTANCE)

        val cocoImports = context.imports
        val globalFieldsAndMethods = context.globalFieldsAndMethods
        val scannerSpecification = context.scannerSpecification
        val pragmas = scannerSpecification?.pragmas
        val productions = context.parserSpecification?.productionList

        if (cocoImports != null && cocoImports.textLength != 0) {
            registrar.addPlace(prefix = targetPackage, element = cocoImports)
            prefixBuilder = StringBuilder()
        }

        prefixBuilder.append("class Parser {\n")
        prefixBuilder.append("\tvoid Recover() {/* generated */}\n")
        prefixBuilder.append("\tpublic static final int _EOF = 0;\n")
        appendSetDecls(prefixBuilder, scannerSpecification?.tokens?.tokenDeclList.orEmpty())
        appendSetDecls(prefixBuilder, pragmas?.pragmaDeclList?.map { it.tokenDecl }.orEmpty(), 102)
        if (globalFieldsAndMethods != null && globalFieldsAndMethods.textLength != 0) {
            registrar.addPlace(prefix = prefixBuilder, element = globalFieldsAndMethods)
            prefixBuilder = StringBuilder()
        }

        prefixBuilder.append("\n\nvoid Get() {\n")

        pragmas?.pragmaDeclList?.mapNotNull { it.semAction?.arbitraryStatements }?.forEach {
            registrar.addPlace(prefix = prefixBuilder, element = it)
            prefixBuilder = StringBuilder()
        }

        prefixBuilder.append("}\n")

        productions?.forEach {
            //            val start = it.textOffset
            prefixBuilder.append("\n")
            var parameterDeclaration = it.formalAttributes?.formalInputAttributes
            val formalOutputAttribute = it.formalAttributes?.formalAttributesWithOutput

            if (formalOutputAttribute != null) {
                parameterDeclaration = formalOutputAttribute.formalInputAttributes
                val embeddedVariableDeclaration = fromJavaVariableDeclaration(
                    formalOutputAttribute
                        .formalOutputAttribute
                        .formalAttributesParameter
                        ?.embeddedVariableDeclaration
                        ?.text
                )
                if (embeddedVariableDeclaration != null) {
                    prefixBuilder.append(embeddedVariableDeclaration.first)
                    prefixBuilder.append(" ")
                } else {
                    prefixBuilder.append("void ")
                }
            } else {
                prefixBuilder.append("void ")
            }

            prefixBuilder.append("${it.name}(")
            if (parameterDeclaration != null) {
                registrar.addPlace(prefix = prefixBuilder, element = parameterDeclaration)
                prefixBuilder = StringBuilder()
            }
            prefixBuilder.append(") {\n")

            if (formalOutputAttribute != null) {
                val formalAttributesParameter = formalOutputAttribute.formalOutputAttribute.formalAttributesParameter
                if (formalAttributesParameter != null) {
                    prefixBuilder.append("\t")
                    registrar.addPlace(prefixBuilder, ";\n", formalAttributesParameter)
                    prefixBuilder = StringBuilder()
                }
            }

            val localDeclaration = it.semAction?.arbitraryStatements
            if (localDeclaration != null) {
                prefixBuilder.append("\t")
                registrar.addPlace(prefixBuilder, "\n\tRecover();\n", localDeclaration)
                prefixBuilder = StringBuilder()
            }

            val factors = PsiTreeUtil.findChildrenOfType(it, me.salzinger.intellij.coco.psi.CocoFactor::class.java)

            factors.forEach {
                val ident = it.ident // != null -> method call
                if (ident != null) {
                    val parameters = it.actualAttributes?.actualAttributesBody // != null -> method params

                    val attributeAssignment = parameters?.attributeAssignment
                    prefixBuilder.append("\t")

                    if (parameters != null) {
                        val varName = attributeAssignment?.embeddedVariableReference
                        val attributeAssignmentLength = attributeAssignment?.textLength ?: 0
                        val additionalOffset = parameters.text.substring(attributeAssignmentLength)
                            .takeWhile { it == ' ' || it == '\t' || it == '\n' || it == '\n' || it == ',' }.length

                        if (varName != null) {
                            registrar.addPlace(prefixBuilder.toString(), " = ", varName)
                            prefixBuilder = StringBuilder()
                        }

                        prefixBuilder.append("${ident.text}(")

                        registrar.addPlace(
                            prefixBuilder,
                            ");\n",
                            TextRange.from(
                                parameters.textOffset + attributeAssignmentLength + additionalOffset,
                                parameters.textLength - attributeAssignmentLength - additionalOffset
                            )
                        )
                        prefixBuilder = StringBuilder()
                    }
                }

                val arbitraryStatements = it.semAction?.arbitraryStatements
                if (arbitraryStatements != null) {
                    prefixBuilder.append("\t")
                    registrar.addPlace(prefixBuilder, "\n\tRecover();\n", arbitraryStatements)
                    prefixBuilder = StringBuilder()
                }
            }

            if (formalOutputAttribute != null) {
                val formalAttributesParameter = formalOutputAttribute.formalOutputAttribute.formalAttributesParameter
                if (formalAttributesParameter != null) {
                    val javaVariableDeclaration =
                        fromJavaVariableDeclaration(formalAttributesParameter.embeddedVariableDeclaration.text)
                    if (javaVariableDeclaration != null) {
                        prefixBuilder.append("return ")
                        prefixBuilder.append(javaVariableDeclaration.second)
                        prefixBuilder.append(";\n")
                    }
                }
            }

            prefixBuilder.append("}\n")
        }

        registrar.addPlace(prefixBuilder, "}", TextRange.from(context.textLength, 0))

        registrar.doneInjecting()
    }

    private fun fromJavaVariableDeclaration(embeddedVariableDeclaration: String?): Pair<String, String>? {
        if (embeddedVariableDeclaration == null) {
            return null
        }

        val parts = embeddedVariableDeclaration.split(Pattern.compile("\\s+"))

        if (parts.size == 2) {
            return parts[0] to parts[1]
        }

        return null
    }

    override fun elementsToInjectIn(): MutableList<out Class<out PsiElement>> {
        return mutableListOf(me.salzinger.intellij.coco.psi.CocoCocoInjectorHost::class.java)
    }

    class LazyMultiHostRegistrarAdapter(val registrar: MultiHostRegistrar, val context: PsiLanguageInjectionHost) :
        MultiHostRegistrar {
        var language: Language? = null
        var started = false

        override fun startInjecting(language: Language): MultiHostRegistrar {
            this.language = language
            return this
        }

        override fun doneInjecting() {
            if (started) registrar.doneInjecting()
        }

        override fun addPlace(
            prefix: String?,
            suffix: String?,
            host: PsiLanguageInjectionHost,
            rangeInsideHost: TextRange
        ): MultiHostRegistrar {
            if (!started) {
                registrar.startInjecting(language!!)
                started = true
            }

            registrar.addPlace(prefix, suffix, host, rangeInsideHost.shiftLeft(context.textOffset))
            return this
        }

        fun addPlace(prefix: String? = null, suffix: String? = null, element: PsiElement): MultiHostRegistrar {
            return addPlace(prefix, suffix, context, element.textRange)
        }

        fun addPlace(prefix: StringBuilder? = null, suffix: String? = null, element: PsiElement): MultiHostRegistrar {
            return addPlace(prefix.toString(), suffix, context, element.textRange)
        }

        fun addPlace(
            prefix: StringBuilder? = null,
            suffix: String? = null,
            rangeInsideHost: TextRange
        ): MultiHostRegistrar {
            return addPlace(prefix.toString(), suffix, context, rangeInsideHost)
        }
    }

    private fun appendSetDecls(
        prefixBuilder: StringBuilder,
        tokenDecls: List<me.salzinger.intellij.coco.psi.CocoTokenDecl>,
        offset: Int = 1
    ) {
        tokenDecls.forEachIndexed { index, cocoTokenDecl ->
            prefixBuilder.append("\tpublic static final int _${cocoTokenDecl.name} = ${index + offset};\n")
        }
    }
}
