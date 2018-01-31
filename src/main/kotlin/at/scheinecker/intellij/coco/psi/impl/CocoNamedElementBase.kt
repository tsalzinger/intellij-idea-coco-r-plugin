package at.scheinecker.intellij.coco.psi.impl

import at.scheinecker.intellij.coco.psi.CocoNamedElement
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.navigation.ItemPresentation
import com.intellij.psi.PsiElement

abstract class CocoNamedElementBase(node: ASTNode) : ASTWrapperPsiElement(node), CocoNamedElement {
    override fun getNameIdentifier(): PsiElement? {
        return CocoPsiImplUtil.getNameIdentifier(this)
    }

    override fun getName(): String? {
        return CocoPsiImplUtil.getName(this)
    }

    override fun setName(p0: String): PsiElement {
        return CocoPsiImplUtil.setName(this, p0)
    }

    override fun getPresentation(): ItemPresentation? {
        return CocoPsiImplUtil.getPresentation(this)
    }
}