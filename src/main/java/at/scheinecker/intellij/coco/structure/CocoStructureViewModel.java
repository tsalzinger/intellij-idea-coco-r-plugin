package at.scheinecker.intellij.coco.structure;

import at.scheinecker.intellij.coco.psi.*;
import com.intellij.ide.structureView.StructureViewModel;
import com.intellij.ide.structureView.StructureViewModelBase;
import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.Sorter;
import com.intellij.openapi.editor.Editor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Thomas on 29/03/2015.
 */
public class CocoStructureViewModel extends StructureViewModelBase implements StructureViewModel.ElementInfoProvider {

    public CocoStructureViewModel(@NotNull CocoFile file, @Nullable Editor editor) {
        super(file, editor, new CocoStructureViewElement(file));
    }

    @NotNull
    @Override
    public Sorter[] getSorters() {
        return new Sorter[]{Sorter.ALPHA_SORTER};
    }

    @Override
    public boolean isAlwaysShowsPlus(StructureViewTreeElement element) {
        return false;
    }

    @Override
    public boolean isAlwaysLeaf(StructureViewTreeElement element) {
        Object value = element.getValue();
        return value instanceof CocoSetDecl
                || value instanceof CocoProduction
                || value instanceof CocoTokenDecl
                || value instanceof CocoCommentDecl
                || value instanceof CocoEnd
                || value instanceof CocoCompiler;
    }
}
