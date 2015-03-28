package Coco;

import at.jku.ssw.coco.intellij.psi.CocoFile;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.util.Function;
import com.intellij.util.containers.ContainerUtil;

import java.util.Collections;
import java.util.List;

/**
 * Created by Thomas on 29/12/2014.
 */
public class CocoRAction extends AnAction {
    public void actionPerformed(AnActionEvent e) {
        final Project project = getEventProject(e);
        final List<CocoFile> bnfFiles = getFiles(e);
        if (project == null || bnfFiles.isEmpty()) return;
        PsiDocumentManager.getInstance(project).commitAllDocuments();
        FileDocumentManager.getInstance().saveAllDocuments();


        for (CocoFile file : bnfFiles) {
            generate(file);
        }
    }

    private static List<CocoFile> getFiles(AnActionEvent e) {
        Project project = getEventProject(e);
        VirtualFile[] files = e.getData(PlatformDataKeys.VIRTUAL_FILE_ARRAY);
        if (project == null || files == null) return Collections.emptyList();
        final PsiManager manager = PsiManager.getInstance(project);
        return ContainerUtil.mapNotNull(files, new Function<VirtualFile, CocoFile>() {
            @Override
            public CocoFile fun(VirtualFile file) {
                PsiFile psiFile = manager.findFile(file);
                return psiFile instanceof CocoFile ? (CocoFile) psiFile : null;
            }
        });
    }

    private void generate(CocoFile file) {
        // TODO use correct output directory
        String path = file.getVirtualFile().getParent().getPath();
        String filePath = file.getVirtualFile().getPath();

        String nsName = null, frameDir = null, outDir = null, ddtString = null;

        Scanner scanner = new Scanner(filePath);
        Parser parser = new Parser(scanner);

        parser.trace = new Trace(path);
        parser.tab = new Tab(parser);
        parser.dfa = new DFA(parser);
        parser.pgen = new ParserGen(parser);

        parser.tab.srcName = filePath;
        parser.tab.srcDir = path;
        parser.tab.nsName = nsName;
        parser.tab.frameDir = (frameDir != null) ? frameDir : path;
        parser.tab.outDir = (outDir != null) ? outDir : path;
        if (ddtString != null) parser.tab.SetDDT(ddtString);

        parser.Parse();

        parser.trace.Close();
        // todo show error / success to user
        System.out.println(parser.errors.count + " errors detected");

        if (parser.errors.count > 0) {
            Messages.showMessageDialog(file.getProject(), parser.errors.count + " errors occured", "Error", Messages.getErrorIcon());
        } else {
            // todo sync virtual file system
            Messages.showMessageDialog(file.getProject(), "Scanner and Parser successfuly generated", "Success", Messages.getInformationIcon());
        }

    }

    @Override
    public void update(AnActionEvent e) {
        List<CocoFile> files = getFiles(e);
        e.getPresentation().setEnabledAndVisible(!files.isEmpty());
    }
}
