package at.scheinecker.intellij.coco.action;

import Coco.*;
import at.scheinecker.intellij.coco.psi.CocoFile;
import com.intellij.compiler.CompilerMessageImpl;
import com.intellij.compiler.impl.CompileContextImpl;
import com.intellij.compiler.impl.FileSetCompileScope;
import com.intellij.compiler.progress.CompilerTask;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.compiler.CompilationStatusListener;
import com.intellij.openapi.compiler.CompilerMessageCategory;
import com.intellij.openapi.compiler.CompilerTopics;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.JavaProjectRootsUtil;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.SourceFolder;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.model.java.JavaModuleSourceRootTypes;
import org.jetbrains.jps.model.java.JavaSourceRootProperties;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Thomas on 29/12/2014.
 */
public class CocoRAction extends AnAction {
    private static final NotificationGroup COCO_NOTIFICATION_GROUP = NotificationGroup.balloonGroup("Coco/R Compiler");

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
                    result.setResult(generate(file, e));
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
        return ContainerUtil.mapNotNull(files, file -> {
            PsiFile psiFile = manager.findFile(file);
            return psiFile instanceof CocoFile ? (CocoFile) psiFile : null;
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

    private VirtualFile generate(@NotNull CocoFile file, @NotNull AnActionEvent actionEvent) {
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

        final IntellijErrors intellijErrors = new IntellijErrors();
        parser.errors = intellijErrors;

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

        try {
            parser.Parse();
        } catch (RuntimeException e) {
            notify(e.getMessage(), MessageType.ERROR);
            return null;
        }

        parser.trace.Close();

        intellijErrors.getErrors()
                .forEach(notification -> notify(notification.getContent(), MessageType.ERROR, true));

        intellijErrors.getWarnings()
                .forEach(notification -> notify(notification.getContent(), MessageType.WARNING, true));


        if (intellijErrors.getErrorCount() != 0) {
            notify("Coco/R Compiler completed with " + intellijErrors.getErrorCount() + " errors", MessageType.ERROR);
        } else if (intellijErrors.getWarningCount() != 0) {
            notify("Coco/R Compiler completed with " + intellijErrors.getWarningCount() + " warnings", MessageType.WARNING);
        } else {
            notify("Coco/R Compiler completed successfully", MessageType.INFO);
        }

        SwingUtilities.invokeLater(() -> {
            final Project eventProject = getEventProject(actionEvent);

            if (eventProject == null || eventProject.isDisposed()) {
                return;
            }

            final CompilationStatusListener publisher = eventProject.getMessageBus().syncPublisher(CompilerTopics.COMPILATION_STATUS);
            final CompilerTask cocoCompilerTask = new CompilerTask(eventProject, "coco", true, false, false, true);
            final CompileContextImpl cocoCompileContext = new CompileContextImpl(eventProject,
                    cocoCompilerTask,
                    new FileSetCompileScope(Collections.singleton(file.getVirtualFile()), Module.EMPTY_ARRAY),
                    true,
                    false
            );

            final Runnable r = () -> {
                intellijErrors.getErrors().forEach(error -> {
                    final CompilerMessageImpl errorMessage = new CompilerMessageImpl(eventProject, CompilerMessageCategory.ERROR, error.getContent(), file.getVirtualFile(), 0, 0, null);
                    cocoCompilerTask.addMessage(errorMessage);
//                    cocoCompileContext.addMessage(CompilerMessageCategory.ERROR, error.getContent(), file.getVirtualFile().getUrl(), 0, 0);
                });

                intellijErrors.getWarnings().forEach(warning -> {
                    final CompilerMessageImpl warningMessage = new CompilerMessageImpl(eventProject, CompilerMessageCategory.WARNING, warning.getContent(), file.getVirtualFile(), 0, 0, null);
                    cocoCompilerTask.addMessage(warningMessage);
//                    cocoCompileContext.addMessage(CompilerMessageCategory.WARNING, warning.getContent(), file.getVirtualFile().getUrl(), 0, 0);
                });
            };

            cocoCompilerTask.start(r, r);

            publisher.compilationFinished(false, intellijErrors.getErrorCount(), intellijErrors.getWarningCount(),
                    cocoCompileContext);
        });

        return outDirFile != null ? outDirFile : parent;
    }

    @Override
    public void update(AnActionEvent e) {
        List<CocoFile> files = getFiles(e);
        e.getPresentation().setEnabledAndVisible(!files.isEmpty());
    }

    private void notify(@NotNull final String message, @NotNull final MessageType messageType) {
        notify(message, messageType, false);
    }

    private void notify(@NotNull final String message, @NotNull final MessageType messageType, final boolean preventBalloon) {
        final Notification notification = COCO_NOTIFICATION_GROUP.createNotification(message, messageType);
        Notifications.Bus.notify(notification);
        if (preventBalloon) {
            notification.hideBalloon();
        }
    }
}
