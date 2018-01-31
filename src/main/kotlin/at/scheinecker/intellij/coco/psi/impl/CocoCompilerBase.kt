package at.scheinecker.intellij.coco.psi.impl

import at.scheinecker.intellij.coco.psi.CocoCompiler
import com.intellij.lang.ASTNode

abstract class CocoCompilerBase(node: ASTNode) : CocoNamedElementBase(node), CocoCompiler {

    override fun getTextOffset(): Int {
        return CocoPsiImplUtil.getTextOffset(this)
    }
}