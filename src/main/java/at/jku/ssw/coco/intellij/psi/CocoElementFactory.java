package at.jku.ssw.coco.intellij.psi;

import at.jku.ssw.coco.intellij.CocoFileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFileFactory;

public class CocoElementFactory {
    public static CocoCompiler createCompiler(Project project, String name) {
        final CocoFile file = createFile(project,
                "COMPILER " + name + "\n"
                        + "PRODUCTIONS\n"
                        + "END " + name + ".");
        return (CocoCompiler) file.getFirstChild().getNextSibling();
    }

    public static CocoFile createFile(Project project, String text) {
        String name = "dummy.ATG";
        return (CocoFile) PsiFileFactory.getInstance(project).
                createFileFromText(name, CocoFileType.INSTANCE, text);
    }
}