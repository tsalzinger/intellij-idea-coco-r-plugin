package me.salzinger.intellij.coco.java

import com.intellij.lang.java.JavaLanguage
import com.intellij.openapi.util.TextRange
import com.intellij.psi.InjectedLanguagePlaces
import com.intellij.psi.LanguageInjector
import com.intellij.psi.PsiLanguageInjectionHost
import me.salzinger.intellij.coco.CocoUtil
import me.salzinger.intellij.coco.settings.CocoConfiguration
import me.salzinger.intellij.coco.settings.CocoInjectionMode

class CocoJavaInjector : LanguageInjector {
    override fun getLanguagesToInject(
        psiLanguageInjectionHost: PsiLanguageInjectionHost,
        injectedLanguagePlaces: InjectedLanguagePlaces
    ) {
        if (CocoConfiguration.getSettings(psiLanguageInjectionHost.project).injectionMode != CocoInjectionMode.SIMPLE) {
            return
        }

        val prefixBuilder = StringBuilder()

        CocoJavaUtil.getTargetPackage(psiLanguageInjectionHost.containingFile.originalFile)
            .ifPresent { prefixBuilder.append("package $it;") }

        if (psiLanguageInjectionHost is me.salzinger.intellij.coco.psi.CocoImports) {
            injectedLanguagePlaces.addPlace(
                JavaLanguage.INSTANCE,
                TextRange(0, psiLanguageInjectionHost.getTextLength()),
                prefixBuilder.toString(),
                ""
            )
        } else {
            prefixBuilder.append("class Parser {\n")
            prefixBuilder.append("\tpublic static final int _EOF = 0;\n")
            appendTokens(prefixBuilder, psiLanguageInjectionHost)
            appendPragmas(prefixBuilder, psiLanguageInjectionHost)

            if (psiLanguageInjectionHost is me.salzinger.intellij.coco.psi.CocoGlobalFieldsAndMethods) {
                injectedLanguagePlaces.addPlace(
                    JavaLanguage.INSTANCE,
                    TextRange(0, psiLanguageInjectionHost.getTextLength()),
                    prefixBuilder.toString(),
                    "}"
                )
            } else if (psiLanguageInjectionHost is me.salzinger.intellij.coco.psi.CocoArbitraryStatements) {
                val globalFieldsAndMethods =
                    CocoUtil.findGlobalFieldsAndMethods(psiLanguageInjectionHost.containingFile.originalFile)
                if (globalFieldsAndMethods != null) {
                    prefixBuilder.append(globalFieldsAndMethods.text)
                }

                val cocoNamedElement = CocoUtil.findNearestCocoNamedElement(psiLanguageInjectionHost)

                if (cocoNamedElement is me.salzinger.intellij.coco.psi.CocoProduction) {
                    val formalAttributes = cocoNamedElement.formalAttributes?.text?.trim('<', '>').orEmpty()
                    prefixBuilder.append("\tvoid ${cocoNamedElement.name}($formalAttributes) {\n")
                } else {
                    prefixBuilder.append("\tvoid ${cocoNamedElement.name}() {\n")
                }

                injectedLanguagePlaces.addPlace(
                    JavaLanguage.INSTANCE,
                    TextRange(0, psiLanguageInjectionHost.getTextLength()),
                    prefixBuilder.toString(),
                    "}}"
                )
            }
        }
    }

    private fun appendTokens(prefixBuilder: StringBuilder, psiLanguageInjectionHost: PsiLanguageInjectionHost) {
        appendTokenDecls(prefixBuilder, CocoUtil.findTokenDecls(psiLanguageInjectionHost.containingFile))
    }

    private fun appendTokenDecls(
        prefixBuilder: StringBuilder,
        tokenDecls: List<me.salzinger.intellij.coco.psi.CocoTokenDecl>,
        offset: Int = 1
    ) {
        tokenDecls.forEachIndexed { index, cocoTokenDecl ->
            prefixBuilder.append("\tpublic static final int _${cocoTokenDecl.name} = ${index + offset};\n")
        }
    }

    private fun appendPragmas(prefixBuilder: StringBuilder, psiLanguageInjectionHost: PsiLanguageInjectionHost) {
        appendTokenDecls(
            prefixBuilder,
            CocoUtil.findPragmaDecls(psiLanguageInjectionHost.containingFile)
                .map { cocoPragmaDecl -> cocoPragmaDecl.tokenDecl },
            102
        )
    }
}
