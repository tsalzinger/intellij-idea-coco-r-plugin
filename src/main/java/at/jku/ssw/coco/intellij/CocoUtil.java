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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Thomas Scheinecker <a href="mailto:tscheinecker@gmail.com">tscheinecker@gmail.com</a>
 */
public class CocoUtil {

    @NotNull
    public static List<String> findCompilerNames(@Nullable Project project) {
        return findCompilers(project)
                .stream()
                .map(CocoCompiler::getName)
                .filter(it -> !Objects.isNull(it))
                .collect(Collectors.toList());
    }

    public static List<CocoCompiler> findCompilers(@Nullable Project project, @NotNull String name) {
        return findCompilers(project)
                .stream()
                .filter(compiler -> name.equals(compiler.getName()))
                .collect(Collectors.toList());
    }

    @NotNull
    public static List<CocoCompiler> findCompilers(@Nullable Project project) {
        if (project == null) {
            return Collections.emptyList();
        }

        Collection<VirtualFile> virtualFiles = FileBasedIndex.getInstance().getContainingFiles(FileTypeIndex.NAME, CocoFileType.INSTANCE, GlobalSearchScope.allScope(project));
        List<CocoCompiler> compilers = new ArrayList<>();
        for (VirtualFile virtualFile : virtualFiles) {
            CocoFile cocoFile = (CocoFile) PsiManager.getInstance(project).findFile(virtualFile);
            if (cocoFile != null) {
                CocoCompiler[] cocoCompilers = PsiTreeUtil.getChildrenOfType(cocoFile, CocoCompiler.class);
                if (cocoCompilers != null) {
                    compilers.addAll(Arrays.asList(cocoCompilers));
                }
            }
        }
        return compilers;
    }
}
