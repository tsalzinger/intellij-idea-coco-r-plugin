package me.salzinger.intellij.coco

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNameIdentifierOwner
import com.intellij.psi.search.searches.ReferencesSearch
import me.salzinger.intellij.coco.psi.HasCocoCharacterReference

class CocoAnnotator : Annotator {

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        when (element) {
            is me.salzinger.intellij.coco.psi.CocoEnd -> {
                val ident = element.nameIdentifier ?: return

                val compilerName = ident.text
                val compiler = CocoUtil.findCompiler(element.getContainingFile(), compilerName)

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
            is HasCocoCharacterReference -> {
                val ident = element.nameIdentifier
                if (ident != null) {
                    val characterReferenceName = element.name
                    val characterDeclaration = me.salzinger.intellij.coco.CocoUtil
                        .findCharacterDeclaration(element.getContainingFile(), characterReferenceName)

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
            is me.salzinger.intellij.coco.psi.CocoFactor -> {
                val ident = element.nameIdentifier
                if (ident != null) {
                    val referenceName = element.name
                    val tokenDecl = CocoUtil.findTokenDecl(element.getContainingFile(), referenceName)
                    val production =
                        CocoUtil.findProduction(element.getContainingFile(), referenceName)

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

                        val formalAttributes =
                            production.formalAttributes?.text.orEmpty().trim('<', '>').trim().split(",")
                                .filter { it.isNotEmpty() }
                        val hasFormalOutputAttribute = formalAttributes.any { it.startsWith("out ") }
                        val hasFormalInputAttributes = formalAttributes.any { !it.startsWith("out ") }

                        val attributes = element.actualAttributes?.text.orEmpty().trim('<', '>').trim().split(",")
                            .filter { it.isNotEmpty() }
                        val outputAttribute = attributes.any { it.startsWith("out ") }
                        val inputAttributes = attributes.any { !it.startsWith("out ") }

                        if (hasFormalInputAttributes && !inputAttributes) {
                            holder
                                .newAnnotation(
                                    HighlightSeverity.ERROR,
                                    "Production '$referenceName' defines formal attributes"
                                )
                                .range(element)
                                .create()
                        } else if (!hasFormalInputAttributes && inputAttributes) {
                            holder
                                .newAnnotation(
                                    HighlightSeverity.ERROR,
                                    "Production '$referenceName' doesn't define formal attributes"
                                )
                                .range(element)
                                .create()
                        }

                        if (!hasFormalOutputAttribute && outputAttribute) {
                            holder
                                .newAnnotation(
                                    HighlightSeverity.ERROR,
                                    "Production '$referenceName' doesn't define an output attributes"
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
            is me.salzinger.intellij.coco.psi.CocoCompiler -> {
                val ident = element.nameIdentifier

                if (ident != null) {
                    holder
                        .newSilentAnnotation(HighlightSeverity.INFORMATION)
                        .range(ident)
                        .textAttributes(DefaultLanguageHighlighterColors.CLASS_NAME)
                        .create()

                    val production = CocoUtil.findProduction(element.getContainingFile(), ident.text)

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
            is PsiNameIdentifierOwner -> {
                val ident = element.nameIdentifier

                if (ident != null) {
                    val first = ReferencesSearch.search(element).findFirst()

                    if (first == null && element is me.salzinger.intellij.coco.psi.CocoProduction) {
                        val compiler =
                            CocoUtil.findCompiler(element.getContainingFile(), element.name)
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
                        val warningText: String = if (element is me.salzinger.intellij.coco.psi.CocoSetDecl) {
                            "Character '${ident.text}' is never used"
                        } else if (element is me.salzinger.intellij.coco.psi.CocoTokenDecl) {
                            if (element.getParent() is me.salzinger.intellij.coco.psi.CocoPragmaDecl) {
                                "Pragma '${ident.text}' is never used"
                            } else {
                                "Token '${ident.text}' is never used"
                            }
                        } else if (element is me.salzinger.intellij.coco.psi.CocoProduction) {
                            "Production '${ident.text}' is never used"
                        } else {
                            "'${ident.text}' is never used"
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
            }
        }
    }
}
