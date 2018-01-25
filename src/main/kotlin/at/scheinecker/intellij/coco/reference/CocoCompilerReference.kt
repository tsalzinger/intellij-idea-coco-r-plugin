package at.scheinecker.intellij.coco.reference

import at.scheinecker.intellij.coco.CocoUtil
import at.scheinecker.intellij.coco.psi.HasCocoCompilerReference
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference

class CocoCompilerReference(element: HasCocoCompilerReference, textRange: TextRange) : AbstractRenamableReference<HasCocoCompilerReference>(element, textRange), PsiReference {

    override fun resolve(): PsiElement? {
        return CocoUtil.findCompiler(myElement.containingFile, myElement.name)
    }

    override fun getVariants(): Array<Any> {
        return CocoUtil.findCompilers(myElement.containingFile).toTypedArray()
    }
}