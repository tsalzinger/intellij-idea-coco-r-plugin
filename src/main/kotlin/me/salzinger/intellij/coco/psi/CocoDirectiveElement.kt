package me.salzinger.intellij.coco.psi

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.navigation.ItemPresentation
import com.intellij.psi.NavigatablePsiElement
import me.salzinger.intellij.coco.psi.impl.CocoPsiImplUtil

abstract class CocoDirectiveElement(node: ASTNode) : ASTWrapperPsiElement(node), NavigatablePsiElement, me.salzinger.intellij.coco.psi.CocoDirective {
    override fun getPresentation(): ItemPresentation? {
        return CocoPsiImplUtil.getPresentation(this)
    }
}