package me.salzinger.intellij.coco.reference

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import me.salzinger.intellij.coco.findByName
import me.salzinger.intellij.coco.findCompilers
import me.salzinger.intellij.coco.psi.HasCocoCompilerReference

class CocoCompilerReference(element: HasCocoCompilerReference, textRange: TextRange) :
    AbstractRenamableReference<HasCocoCompilerReference>(element, textRange), PsiReference {

    override fun resolve(): PsiElement? {
        return findCompilers(myElement.containingFile).findByName(myElement.name)
    }

    override fun getVariants(): Array<Any> {
        return findCompilers(myElement.containingFile).toTypedArray()
    }
}
