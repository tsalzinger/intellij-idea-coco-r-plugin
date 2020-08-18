package me.salzinger.intellij.coco.psi.impl

import com.intellij.lang.ASTNode
import me.salzinger.intellij.coco.psi.CocoCompiler

abstract class CocoCompilerBase(node: ASTNode) :
    CocoNamedElementBase(node),
    CocoCompiler {

    override fun getTextOffset(): Int {
        return CocoPsiImplUtil.getTextOffset(this)
    }
}
