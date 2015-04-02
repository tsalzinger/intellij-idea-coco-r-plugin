package Coco;

import at.jku.ssw.coco.intellij.psi.CocoFile;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.JavaProjectRootsUtil;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.SourceFolder;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.util.Function;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.model.java.JavaModuleSourceRootTypes;
import org.jetbrains.jps.model.java.JavaSourceRootProperties;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Thomas on 29/12/2014.
 */
public class CocoRAction extends AnAction {
    public void actionPerformed(AnActionEvent e) {
        final Project project = getEventProject(e);
        final List<CocoFile> bnfFiles = getFiles(e);
        if (project == null || bnfFiles.isEmpty()) {
            return;
        }

        PsiDocumentManager.getInstance(project).commitAllDocuments();
        FileDocumentManager.getInstance().saveAllDocuments();


        for (CocoFile file : bnfFiles) {
            VirtualFile result = new WriteAction<VirtualFile>() {
                @Override
                protected void run(@NotNull Result<VirtualFile> result) throws Throwable {
                    result.setResult(generate(file));
                }
            }.execute().throwException().getResultObject();


            if (result != null) {
                VfsUtil.markDirtyAndRefresh(false, true, true, result);
            }
        }
    }

    @NotNull
    private static List<CocoFile> getFiles(@NotNull AnActionEvent e) {
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

    @NotNull
    private List<SourceFolder> getGeneratedSourceFolders(@NotNull CocoFile file) {
        Module module = ModuleUtil.findModuleForFile(file.getVirtualFile(), file.getProject());
        if (module != null) {
            List<SourceFolder> sourceFolders = new ArrayList<>();

            for (ContentEntry entry : ModuleRootManager.getInstance(module).getContentEntries()) {
                for (SourceFolder folder : entry.getSourceFolders()) {
                    JavaSourceRootProperties properties = folder.getJpsElement().getProperties(JavaModuleSourceRootTypes.SOURCES);
                    if (properties != null && properties.isForGeneratedSources()) {
                        sourceFolders.add(folder);
                    }
                }
            }
            return sourceFolders;
        }

        return Collections.emptyList();
    }

    private VirtualFile generate(@NotNull CocoFile file) {
        String filePackage = null;
        PsiDirectory containingDirectory = file.getContainingDirectory();
        if (containingDirectory != null) {
            PsiPackage psiFilePackage = JavaDirectoryService.getInstance().getPackage(containingDirectory);
            if (psiFilePackage != null) {
                filePackage = psiFilePackage.getQualifiedName();
            }
        }
        VirtualFile outDirFile = null;
        List<SourceFolder> generatedSourceFolders = getGeneratedSourceFolders(file);
        for (SourceFolder generatedSourceFolder : generatedSourceFolders) {
            VirtualFile generatedSourceFolderFile = generatedSourceFolder.getFile();
            if (generatedSourceFolderFile != null) {
                outDirFile = generatedSourceFolderFile;
                break;
            }
        }

        if (outDirFile == null) {
            List<VirtualFile> suitableDestinationSourceRoots = JavaProjectRootsUtil.getSuitableDestinationSourceRoots(file.getProject());
            if (!suitableDestinationSourceRoots.isEmpty()) {
                outDirFile = suitableDestinationSourceRoots.get(0);
            }
        }

        if (outDirFile != null && filePackage != null) {
            try {
                outDirFile = VfsUtil.createDirectoryIfMissing(outDirFile, filePackage.replace(".", "/"));
            } catch (IOException ignore) {
            }
        }

        VirtualFile parent = file.getVirtualFile().getParent();
        String path = parent.getPath();
        String filePath = file.getVirtualFile().getPath();

        String frameDir = null, outDir = (outDirFile != null) ? outDirFile.getPath() : null, ddtString = null;

        Scanner scanner = new Scanner(filePath);
        Parser parser = new Parser(scanner);

        parser.trace = new Trace(path);
        parser.tab = new Tab(parser);
        parser.dfa = new DFA(parser);
        parser.pgen = new ParserGen(parser);

        parser.tab.srcName = filePath;
        parser.tab.srcDir = path;
        parser.tab.nsName = filePackage;
        parser.tab.frameDir = (frameDir != null) ? frameDir : path;
        parser.tab.outDir = (outDir != null) ? outDir : path;
        if (ddtString != null) {
            parser.tab.SetDDT(ddtString);
        }

        parser.Parse();

        parser.trace.Close();
        // todo show error / success to user
        System.out.println(parser.errors.count + " errors detected");

        if (parser.errors.count > 0) {
            Messages.showMessageDialog(file.getProject(), parser.errors.count + " errors occured", "Error", Messages.getErrorIcon());
            return null;
        }

        Messages.showMessageDialog(file.getProject(), "Scanner and Parser successfuly generated", "Success", Messages.getInformationIcon());
        if (outDirFile != null) {
            return outDirFile;
        }

        return parent;
    }

    @Override
    public void update(AnActionEvent e) {
        List<CocoFile> files = getFiles(e);
        e.getPresentation().setEnabledAndVisible(!files.isEmpty());
    }
}
