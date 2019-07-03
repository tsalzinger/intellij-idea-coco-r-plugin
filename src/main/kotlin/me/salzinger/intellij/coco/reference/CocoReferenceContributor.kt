package me.salzinger.intellij.coco.reference

import com.intellij.openapi.util.TextRange
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.*
import com.intellij.util.ProcessingContext
import org.apache.commons.lang.StringUtils

class CocoReferenceContributor : PsiReferenceContributor() {
    private fun getRelativeTextRange(element: PsiElement): TextRange {
        val startOffsetInParent = element.startOffsetInParent
        return TextRange(startOffsetInParent, startOffsetInParent + element.textLength)
    }

    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        register(registrar, ::CocoCompilerReference)
        register(registrar, ::CocoCharacterReference)
        register(registrar, ::CocoProductionReference)
        register(registrar, ::CocoTokenReference)
    }

    private inline fun <reified T : PsiNameIdentifierOwner> register(registrar: PsiReferenceRegistrar, crossinline psiReferenceProvider: (T, TextRange) -> PsiReference) {
        registrar.registerReferenceProvider(PlatformPatterns.psiElement(T::class.java),
                object : PsiReferenceProvider() {
                    override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
                        val hasIdent = element as T
                        val ident = hasIdent.nameIdentifier

                        if (ident != null) {
                            if (StringUtils.isNotBlank(hasIdent.name)) {
                                return arrayOf(psiReferenceProvider.invoke(hasIdent, getRelativeTextRange(ident)))
                            }
                        }

                        return PsiReference.EMPTY_ARRAY
                    }
                })
    }
}