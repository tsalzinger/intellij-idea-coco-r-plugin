package at.scheinecker.intellij.coco.reference

import at.scheinecker.intellij.coco.CocoUtil
import at.scheinecker.intellij.coco.psi.HasCocoTokenReference
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference

class CocoTokenReference(element: HasCocoTokenReference, textRange: TextRange) : AbstractRenamableReference<HasCocoTokenReference>(element, textRange), PsiReference {

    override fun resolve(): PsiElement? {
        return CocoUtil.findTokenDecl(myElement.containingFile, myElement.name)
    }

    override fun getVariants(): Array<Any> {
        return CocoUtil.findTokenDecls(myElement.containingFile)
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