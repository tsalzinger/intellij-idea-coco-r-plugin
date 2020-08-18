package me.salzinger.intellij.coco

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNameIdentifierOwner
import com.intellij.psi.search.searches.ReferencesSearch
import me.salzinger.intellij.coco.psi.CocoCompiler
import me.salzinger.intellij.coco.psi.CocoEnd
import me.salzinger.intellij.coco.psi.CocoFactor
import me.salzinger.intellij.coco.psi.CocoPragmaDecl
import me.salzinger.intellij.coco.psi.CocoProduction
import me.salzinger.intellij.coco.psi.CocoSetDecl
import me.salzinger.intellij.coco.psi.CocoTokenDecl
import me.salzinger.intellij.coco.psi.HasCocoCharacterReference

class CocoAnnotator : Annotator {

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        when (element) {
            is CocoEnd -> {
                annotateCocoEnd(element, holder)
            }
            is HasCocoCharacterReference -> {
                annotateHasCocoCharacterReference(element, holder)
            }
            is CocoFactor -> {
                annotateCocoFactor(element, holder)
            }
            is CocoCompiler -> {
                annotateCocoCompiler(element, holder)
            }
            is PsiNameIdentifierOwner -> {
                annotatePsiNameIdentifierOwner(element, holder)
            }
        }
    }

    private fun annotatePsiNameIdentifierOwner(
        element: PsiNameIdentifierOwner,
        holder: AnnotationHolder,
    ) {
        val ident = element.nameIdentifier ?: return

        val first = ReferencesSearch.search(element).findFirst()

        if (first == null && element is CocoProduction) {
            val compiler =
                findCompilers(element.getContainingFile()).findByName(element.name)
            if (compiler != null) {
                holder
                    .newAnnotation(
                        HighlightSeverity.INFORMATION,
                        "Entry point production for grammar"
                    )
                    .range(ident)
                    .textAttributes(DefaultLanguageHighlighterColors.INSTANCE_FIELD)
                    .create()
                return
            }
        }

        if (first == null) {
            val warningText: String = when (element) {
                is CocoSetDecl -> {
                    "Character '${ident.text}' is never used"
                }
                is CocoTokenDecl -> {
                    if (element.getParent() is CocoPragmaDecl) {
                        "Pragma '${ident.text}' is never used"
                    } else {
                        "Token '${ident.text}' is never used"
                    }
                }
                is CocoProduction -> {
                    "Production '${ident.text}' is never used"
                }
                else -> {
                    "'${ident.text}' is never used"
                }
            }

            holder
                .newAnnotation(
                    HighlightSeverity.WARNING,
                    warningText
                )
                .range(ident)
                .textAttributes(DefaultLanguageHighlighterColors.LINE_COMMENT)
                .create()
        } else {
            holder
                .newSilentAnnotation(HighlightSeverity.INFORMATION)
                .range(ident)
                .textAttributes(DefaultLanguageHighlighterColors.INSTANCE_FIELD)
                .create()
        }
    }

    private fun annotateCocoCompiler(
        element: CocoCompiler,
        holder: AnnotationHolder,
    ) {
        val ident = element.nameIdentifier

        if (ident != null) {
            holder
                .newSilentAnnotation(HighlightSeverity.INFORMATION)
                .range(ident)
                .textAttributes(DefaultLanguageHighlighterColors.CLASS_NAME)
                .create()

            val production = findProductions(element.containingFile)
                .findByName(ident.text)

            if (production == null) {
                holder
                    .newAnnotation(
                        HighlightSeverity.ERROR,
                        "Missing production for '" + ident.text + "'"
                    )
                    .range(ident)
                    .create()
            }
        }
    }

    private fun annotateCocoFactor(
        element: CocoFactor,
        holder: AnnotationHolder,
    ) {
        val ident = element.nameIdentifier
        if (ident != null) {
            val referenceName = element.name
            val tokenDecl = findTokenDecls(element.containingFile).findByName(referenceName)
            val production = findProductions(element.containingFile).findByName(referenceName)

            if (tokenDecl != null) {
                holder
                    .newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(ident)
                    .create()
            } else if (production != null) {
                holder
                    .newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(ident)
                    .textAttributes(DefaultLanguageHighlighterColors.INSTANCE_FIELD)
                    .create()

                val formalAttributes = Attributes.of(production.formalAttributes?.text)
                val actualAttributes = Attributes.of(element.actualAttributes?.text)

                if (formalAttributes.hasInputAttributes && !actualAttributes.hasInputAttributes) {
                    holder
                        .newAnnotation(
                            HighlightSeverity.ERROR,
                            "Production '$referenceName' defines formal attributes"
                        )
                        .range(element)
                        .create()
                } else if (!formalAttributes.hasInputAttributes && actualAttributes.hasInputAttributes) {
                    holder
                        .newAnnotation(
                            HighlightSeverity.ERROR,
                            "Production '$referenceName' doesn't define formal attributes"
                        )
                        .range(element)
                        .create()
                }

                if (!formalAttributes.hasOutputAttributes && actualAttributes.hasOutputAttributes) {
                    holder
                        .newAnnotation(
                            HighlightSeverity.ERROR,
                            "Production '$referenceName' doesn't define output attributes"
                        )
                        .range(element)
                        .create()
                }
            } else {
                holder
                    .newAnnotation(
                        HighlightSeverity.ERROR,
                        "Unresolved Token or Production '$referenceName'"
                    )
                    .range(ident)
                    .create()
            }
        }
    }

    private fun annotateHasCocoCharacterReference(
        element: HasCocoCharacterReference,
        holder: AnnotationHolder,
    ) {
        val ident = element.nameIdentifier
        if (ident != null) {
            val characterReferenceName = element.name
            val characterDeclaration = findCharacterDeclarations(element.containingFile)
                .findByName(characterReferenceName)

            if (characterDeclaration != null) {
                val characterOffset = characterDeclaration.textRange.startOffset
                val referenceOffset = element.textRange.startOffset

                if (referenceOffset < characterOffset) {
                    holder
                        .newAnnotation(
                            HighlightSeverity.ERROR,
                            "Character '$characterReferenceName' used before it is defined"
                        )
                        .range(ident)
                        .create()
                } else {
                    holder
                        .newSilentAnnotation(HighlightSeverity.INFORMATION)
                        .range(ident)
                        .textAttributes(DefaultLanguageHighlighterColors.INSTANCE_FIELD)
                        .create()
                }
            } else {
                holder
                    .newAnnotation(
                        HighlightSeverity.ERROR,
                        "Unresolved Character '$characterReferenceName'"
                    )
                    .range(ident)
                    .create()
            }
        }
    }

    private fun annotateCocoEnd(
        element: CocoEnd,
        holder: AnnotationHolder,
    ) {
        val ident = element.nameIdentifier ?: return

        val compilerName = ident.text
        val compiler = findCompilers(element.containingFile).findByName(compilerName)

        if (compiler != null) {
            holder
                .newSilentAnnotation(
                    HighlightSeverity.INFORMATION
                )
                .range(ident)
                .textAttributes(DefaultLanguageHighlighterColors.CLASS_REFERENCE)
                .create()
        } else {
            holder
                .newAnnotation(
                    HighlightSeverity.ERROR,
                    "Unresolved COMPILER '$compilerName'"
                )
                .range(ident)
                .create()
        }
    }

    private data class Attributes(
        val attributes: List<String>,
    ) {
        val hasOutputAttributes: Boolean by lazy {
            attributes.any { it.startsWith("out ") }
        }

        val hasInputAttributes: Boolean by lazy {
            attributes.any { !it.startsWith("out ") }
        }

        companion object {
            fun of(declaration: String?): Attributes {
                return Attributes(declaration.toAttributesDeclaration())
            }

            private fun String?.toAttributesDeclaration(): List<String> {
                return orEmpty()
                    .trim('<', '>')
                    .trim()
                    .split(",")
                    .filter { it.isNotBlank() }
            }
        }
    }
}
