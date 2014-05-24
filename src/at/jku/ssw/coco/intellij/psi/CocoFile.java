package at.jku.ssw.coco.intellij.psi;

import at.jku.ssw.coco.intellij.CocoFileType;
import at.jku.ssw.coco.intellij.CocoLanguage;
import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Scheinecker <a href="mailto:tscheinecker@gmail.com">tscheinecker@gmail.com</a>
 */
public class CocoFile extends PsiFileBase {
    public CocoFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, CocoLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return CocoFileType.INSTANCE;
    }

    @Override
    public String toString() {
        return "Coco File";
    }
}
