package me.salzinger.intellij.coco.structure

import com.intellij.ide.structureView.StructureViewModel
import com.intellij.ide.structureView.StructureViewModelBase
import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.ide.util.treeView.smartTree.Sorter
import com.intellij.openapi.editor.Editor
import me.salzinger.intellij.coco.psi.CocoDirectiveElement
import me.salzinger.intellij.coco.psi.CocoFile

/**
 * Created by Thomas on 29/03/2015.
 */
class CocoStructureViewModel(file: CocoFile, editor: Editor?) : StructureViewModelBase(file, editor, CocoStructureViewElement(file)), StructureViewModel.ElementInfoProvider {

    override fun getSorters(): Array<Sorter> {
        return arrayOf(Sorter.ALPHA_SORTER)
    }

    override fun isAlwaysShowsPlus(element: StructureViewTreeElement): Boolean {
        return false
    }

    override fun isAlwaysLeaf(element: StructureViewTreeElement): Boolean {
        val value = element.value
        return (value is me.salzinger.intellij.coco.psi.CocoSetDecl
                || value is CocoDirectiveElement
                || value is me.salzinger.intellij.coco.psi.CocoProduction
                || value is me.salzinger.intellij.coco.psi.CocoTokenDecl
                || value is me.salzinger.intellij.coco.psi.CocoCommentDecl
                || value is me.salzinger.intellij.coco.psi.CocoEnd
                || value is me.salzinger.intellij.coco.psi.CocoCompiler)
    }
}
