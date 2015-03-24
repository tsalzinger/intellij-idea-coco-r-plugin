package at.jku.ssw.coco.intellij;

import at.jku.ssw.coco.intellij.psi.CocoCompiler;
import at.jku.ssw.coco.intellij.psi.CocoFile;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.indexing.FileBasedIndex;

import java.util.Collection;

/**
 * @author Thomas Scheinecker <a href="mailto:tscheinecker@gmail.com">tscheinecker@gmail.com</a>
 */
public class CocoUtil {
    public static String getCompilerName(Project project) {
        Collection<VirtualFile> virtualFiles = FileBasedIndex.getInstance().getContainingFiles(FileTypeIndex.NAME, CocoFileType.INSTANCE, GlobalSearchScope.allScope(project));
        for (VirtualFile virtualFile : virtualFiles) {
            CocoFile cocoFile = (CocoFile) PsiManager.getInstance(project).findFile(virtualFile);
            if (cocoFile != null) {
                CocoCompiler[] cocoCompilers = PsiTreeUtil.getChildrenOfType(cocoFile, CocoCompiler.class);
                if (cocoCompilers != null && cocoCompilers.length == 1) {
                    return cocoCompilers[0].getCompilerName();
                }
            }
        }
        return null;
    }
}
