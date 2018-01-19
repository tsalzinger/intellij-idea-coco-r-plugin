package at.scheinecker.intellij.coco.structure

import at.scheinecker.intellij.coco.psi.*
import com.intellij.ide.structureView.StructureViewModel
import com.intellij.ide.structureView.StructureViewModelBase
import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.ide.util.treeView.smartTree.Sorter
import com.intellij.openapi.editor.Editor

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
        return (value is CocoSetDecl
                || value is CocoDirectiveElement
                || value is CocoProduction
                || value is CocoTokenDecl
                || value is CocoCommentDecl
                || value is CocoEnd
                || value is CocoCompiler)
    }
}
