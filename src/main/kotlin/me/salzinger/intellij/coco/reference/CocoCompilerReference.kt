package me.salzinger.intellij.coco.reference

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import me.salzinger.intellij.coco.CocoUtil
import me.salzinger.intellij.coco.psi.HasCocoCompilerReference

class CocoCompilerReference(element: HasCocoCompilerReference, textRange: TextRange) :
    AbstractRenamableReference<HasCocoCompilerReference>(element, textRange), PsiReference {

    override fun resolve(): PsiElement? {
        return CocoUtil.findCompiler(myElement.containingFile, myElement.name)
    }

    override fun getVariants(): Array<Any> {
        return CocoUtil.findCompilers(myElement.containingFile).toTypedArray()
    }
}
