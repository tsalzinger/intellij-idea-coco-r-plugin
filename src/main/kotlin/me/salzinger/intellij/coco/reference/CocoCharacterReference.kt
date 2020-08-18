package me.salzinger.intellij.coco.reference

import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import me.salzinger.intellij.coco.findByName
import me.salzinger.intellij.coco.findCharacterDeclarations
import me.salzinger.intellij.coco.psi.HasCocoCharacterReference

class CocoCharacterReference(element: HasCocoCharacterReference, textRange: TextRange) :
    AbstractRenamableReference<HasCocoCharacterReference>(element, textRange), PsiReference {

    override fun resolve(): PsiElement? {
        return findCharacterDeclarations(myElement.containingFile).findByName(myElement.name)
    }

    override fun getVariants(): Array<Any> {
        return findCharacterDeclarations(myElement.containingFile)
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
