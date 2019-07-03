package me.salzinger.intellij.coco.structure

import com.intellij.ide.structureView.StructureViewBuilder
import com.intellij.ide.structureView.StructureViewModel
import com.intellij.ide.structureView.TreeBasedStructureViewBuilder
import com.intellij.lang.PsiStructureViewFactory
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiFile
import me.salzinger.intellij.coco.psi.CocoFile

/**
 * Created by Thomas on 29/03/2015.
 */
class CocoStructureViewFactory : PsiStructureViewFactory {
    override fun getStructureViewBuilder(psiFile: PsiFile): StructureViewBuilder? {
        return if (psiFile !is CocoFile) {
            null
        } else object : TreeBasedStructureViewBuilder() {
            override fun createStructureViewModel(editor: Editor?): StructureViewModel {
                return CocoStructureViewModel(psiFile, editor)
            }

            override fun isRootNodeShown(): Boolean {
                return false
            }
        }

    }
}
