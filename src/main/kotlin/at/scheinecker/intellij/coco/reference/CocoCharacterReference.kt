package at.scheinecker.intellij.coco.reference

import at.scheinecker.intellij.coco.CocoUtil
import at.scheinecker.intellij.coco.psi.HasCocoCharacterReference
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference

class CocoCharacterReference(element: HasCocoCharacterReference, textRange: TextRange) : AbstractRenamableReference<HasCocoCharacterReference>(element, textRange), PsiReference {

    override fun resolve(): PsiElement? {
        return CocoUtil.findCharacterDeclaration(myElement.containingFile, myElement.name)
    }

    override fun getVariants(): Array<Any> {
        return CocoUtil.findCharacterDeclarations(myElement.containingFile)
                .map { it ->
                    val set = it.set
                    val lookupElementBuilder = LookupElementBuilder.create(it)
                            .withTypeText("Character")


                    if (set != null) {
                        return@map lookupElementBuilder.withTailText(" = " + set.text, true)
                    }

                    lookupElementBuilder
                }
                .toTypedArray()
    }
}