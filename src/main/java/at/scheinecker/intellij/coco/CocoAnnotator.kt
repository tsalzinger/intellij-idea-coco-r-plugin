package at.scheinecker.intellij.coco

import at.scheinecker.intellij.coco.psi.*
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNameIdentifierOwner
import com.intellij.psi.search.searches.ReferencesSearch

class CocoAnnotator : Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (element is CocoEnd) {
            val ident = element.nameIdentifier ?: return

            val compilerName = ident.text
            val compiler = CocoUtil.findCompiler(element.getContainingFile(), compilerName)

            if (compiler != null) {
                val annotation = holder.createInfoAnnotation(ident.textRange, null)
                annotation.setTextAttributes(DefaultLanguageHighlighterColors.CLASS_REFERENCE)
            } else {
                holder.createErrorAnnotation(ident.textRange, "Unresolved COMPILER '$compilerName'")
            }
        } else if (element is HasCocoCharacterReference) {
            val ident = element.nameIdentifier
            if (ident != null) {
                val characterReferenceName = element.name
                val characterDeclaration = CocoUtil.findCharacterDeclaration(element.getContainingFile(), characterReferenceName)

                if (characterDeclaration != null) {
                    val characterOffset = characterDeclaration.textRange.startOffset
                    val referenceOffset = element.textRange.startOffset

                    if (referenceOffset < characterOffset) {
                        holder.createErrorAnnotation(ident, "Character '$characterReferenceName' used before its defined")
                    } else {
                        val annotation = holder.createInfoAnnotation(ident, null)
                        annotation.setTextAttributes(DefaultLanguageHighlighterColors.INSTANCE_FIELD)
                    }
                } else {
                    holder.createErrorAnnotation(ident.textRange, "Unresolved Character '$characterReferenceName'")
                }
            }
        } else if (element is CocoFactor) {
            val ident = element.nameIdentifier
            if (ident != null) {
                val referenceName = element.name
                val tokenDecl = CocoUtil.findTokenDecl(element.getContainingFile(), referenceName)
                val production = CocoUtil.findProduction(element.getContainingFile(), referenceName)

                if (tokenDecl != null) {
                    val characterOffset = tokenDecl.textRange.startOffset
                    val referenceOffset = element.textRange.startOffset

                    if (referenceOffset < characterOffset) {
                        holder.createWarningAnnotation(ident, "Token '$referenceName' used before its defined")
                    } else {
                        val annotation = holder.createInfoAnnotation(ident, null)
                        annotation.setTextAttributes(DefaultLanguageHighlighterColors.INSTANCE_FIELD)
                    }
                } else if (production != null) {
                    val annotation = holder.createInfoAnnotation(ident, null)
                    annotation.textAttributes = DefaultLanguageHighlighterColors.INSTANCE_FIELD

                    if (production.formalAttributes != null && element.actualAttributes == null) {
                        holder.createErrorAnnotation(ident.textRange, "Production '$referenceName' defines formal attributes")
                    } else if (production.formalAttributes == null && element.actualAttributes != null) {
                        holder.createErrorAnnotation(element.actualAttributes!!.textRange, "Production '$referenceName' doesn't define formal attributes")
                    }
                } else {
                    holder.createErrorAnnotation(ident.textRange, "Unresolved Token or Production '$referenceName'")
                }
            }
        } else if (element is CocoCompiler) {
            val ident = element.nameIdentifier

            if (ident != null) {
                val annotation = holder.createInfoAnnotation(ident, null)
                annotation.textAttributes = DefaultLanguageHighlighterColors.CLASS_NAME
                val production = CocoUtil.findProduction(element.getContainingFile(), ident.text)

                if (production == null) {
                    holder.createErrorAnnotation(ident, "Missing production for '" + ident.text + "'")
                }
            }

        } else if (element is PsiNameIdentifierOwner) {
            val ident = element.nameIdentifier

            if (ident != null) {
                val first = ReferencesSearch.search(element).findFirst()

                if (first == null && element is CocoProduction) {
                    val compiler = CocoUtil.findCompiler(element.getContainingFile(), element.name)
                    if (compiler != null) {
                        val annotation = holder.createInfoAnnotation(ident, "Main production for grammar")
                        annotation.textAttributes = DefaultLanguageHighlighterColors.INSTANCE_FIELD
                        return
                    }
                }

                if (first == null) {
                    val warningText: String

                    if (element is CocoSetDecl) {
                        warningText = "Character '" + ident.text + "' is never used"
                    } else if (element is CocoTokenDecl) {
                        if (element.getParent() is CocoPragmaDecl) {
                            warningText = "Pragma '" + ident.text + "' is never used"
                        } else {
                            warningText = "Token '" + ident.text + "' is never used"
                        }
                    } else if (element is CocoProduction) {
                        warningText = "Production '" + ident.text + "' is never used"
                    } else {
                        warningText = "'" + ident.text + "' is never used"
                    }

                    val annotation = holder.createWeakWarningAnnotation(ident, warningText)
                    annotation.textAttributes = DefaultLanguageHighlighterColors.LINE_COMMENT
                } else {
                    val annotation = holder.createInfoAnnotation(ident, null)
                    annotation.textAttributes = DefaultLanguageHighlighterColors.INSTANCE_FIELD
                }
            }
        }
    }
}