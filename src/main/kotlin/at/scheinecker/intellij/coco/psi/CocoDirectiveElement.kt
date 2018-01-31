package at.scheinecker.intellij.coco.psi

import at.scheinecker.intellij.coco.psi.impl.CocoPsiImplUtil
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.navigation.ItemPresentation
import com.intellij.psi.NavigatablePsiElement

abstract class CocoDirectiveElement(node: ASTNode) : ASTWrapperPsiElement(node), NavigatablePsiElement {
    override fun getPresentation(): ItemPresentation? {
        return CocoPsiImplUtil.getPresentation(this)
    }
}