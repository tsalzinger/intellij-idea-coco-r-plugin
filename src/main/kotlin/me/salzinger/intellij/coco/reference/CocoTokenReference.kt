package me.salzinger.intellij.coco.reference

import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import me.salzinger.intellij.coco.findByName
import me.salzinger.intellij.coco.findTokenDecls
import me.salzinger.intellij.coco.psi.HasCocoTokenReference

class CocoTokenReference(element: HasCocoTokenReference, textRange: TextRange) :
    AbstractRenamableReference<HasCocoTokenReference>(element, textRange), PsiReference {

    override fun resolve(): PsiElement? {
        return findTokenDecls(myElement.containingFile)
            .findByName(myElement.name)
    }

    override fun getVariants(): Array<Any> {
        return findTokenDecls(myElement.containingFile)
            .map { it ->
                val tokenExpr = it.tokenExpr

                val lookupElementBuilder = LookupElementBuilder.create(it)
                    .withTypeText("Token")

                if (tokenExpr != null) {
                    return@map lookupElementBuilder.withTailText(" = " + tokenExpr.text, true)
                }

                lookupElementBuilder
            }
            .toTypedArray()
    }
}
