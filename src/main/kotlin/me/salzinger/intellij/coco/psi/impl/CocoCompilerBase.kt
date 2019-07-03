package me.salzinger.intellij.coco.psi.impl

import com.intellij.lang.ASTNode

abstract class CocoCompilerBase(node: ASTNode) : CocoNamedElementBase(node), me.salzinger.intellij.coco.psi.CocoCompiler {

    override fun getTextOffset(): Int {
        return CocoPsiImplUtil.getTextOffset(this)
    }
}