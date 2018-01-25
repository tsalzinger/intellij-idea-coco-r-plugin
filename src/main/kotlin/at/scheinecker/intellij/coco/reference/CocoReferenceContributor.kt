package at.scheinecker.intellij.coco.reference

import at.scheinecker.intellij.coco.psi.HasCocoCharacterReference
import at.scheinecker.intellij.coco.psi.HasCocoCompilerReference
import at.scheinecker.intellij.coco.psi.HasCocoProductionReference
import at.scheinecker.intellij.coco.psi.HasCocoTokenReference
import com.intellij.openapi.util.TextRange
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.*
import com.intellij.util.ProcessingContext
import org.apache.commons.lang.StringUtils

import java.util.function.BiFunction

class CocoReferenceContributor : PsiReferenceContributor() {
    private fun getRelativeTextRange(element: PsiElement): TextRange {
        val startOffsetInParent = element.startOffsetInParent
        return TextRange(startOffsetInParent, startOffsetInParent + element.textLength)
    }

    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        register(registrar, HasCocoCompilerReference::class.java, BiFunction { element, textRange -> CocoCompilerReference(element, textRange) })
        register(registrar, HasCocoCharacterReference::class.java, BiFunction { element, textRange -> CocoCharacterReference(element, textRange) })
        register(registrar, HasCocoProductionReference::class.java, BiFunction { element, textRange -> CocoProductionReference(element, textRange) })
        register(registrar, HasCocoTokenReference::class.java, BiFunction { element, textRange -> CocoTokenReference(element, textRange) })
    }

    private fun <T : PsiNameIdentifierOwner> register(registrar: PsiReferenceRegistrar, hasIdentClass: Class<T>, psiReferenceProvider: BiFunction<T, TextRange, PsiReference>) {
        registrar.registerReferenceProvider(PlatformPatterns.psiElement(hasIdentClass),
                object : PsiReferenceProvider() {
                    override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
                        val hasIdent = element as T
                        val ident = hasIdent.nameIdentifier

                        if (ident != null) {
                            if (StringUtils.isNotBlank(hasIdent.name)) {
                                return arrayOf(psiReferenceProvider.apply(hasIdent, getRelativeTextRange(ident)))
                            }
                        }

                        return PsiReference.EMPTY_ARRAY
                    }
                })
    }
}