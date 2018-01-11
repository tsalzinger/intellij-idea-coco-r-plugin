package at.scheinecker.intellij.coco.action;

import Coco.*;
import at.scheinecker.intellij.coco.CocoUtil;
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
import java.util.Optional;

/**
 * Created by Thomas on 29/12/2014.
 */
public class CocoRAction extends AnAction {
    private static final NotificationGroup COCO_NOTIFICATION_GROUP = NotificationGroup.balloonGroup("Coco/R Compiler");

    public void actionPerformed(AnActionEvent e) {
        final Project project = getEventProject(e);
        final List<CocoFile> bnfFiles = getFiles(e);
        if (project == null || bnfFiles.isEmpty()) {
            // do nothing if no bnf file was selected
            return;
        }

        // ensure all changes are actually persisted to the file system
        PsiDocumentManager.getInstance(project).commitAllDocuments();
        FileDocumentManager.getInstance().saveAllDocuments();

        for (CocoFile file : bnfFiles) {
            WriteAction
                    .compute(() -> generate(file, e))
                    .ifPresent(this::markFileTreeAsDirtyAndReload);
        }
    }

    private void markFileTreeAsDirtyAndReload(final VirtualFile virtualFile) {
        VfsUtil.markDirtyAndRefresh(false, true, true, virtualFile);
    }

    @NotNull
    private static List<CocoFile> getFiles(@NotNull AnActionEvent e) {
        final Project project = getEventProject(e);
        final VirtualFile[] files = e.getData(PlatformDataKeys.VIRTUAL_FILE_ARRAY);
        if (project == null || files == null) return Collections.emptyList();
        final PsiManager manager = PsiManager.getInstance(project);
        return ContainerUtil.mapNotNull(files, file -> {
            final PsiFile psiFile = manager.findFile(file);
            return psiFile instanceof CocoFile ? (CocoFile) psiFile : null;
        });
    }

    @NotNull
    private List<SourceFolder> getGeneratedSourceFolders(@NotNull CocoFile file) {
        final Module module = ModuleUtil.findModuleForFile(file.getVirtualFile(), file.getProject());
        if (module != null) {
            final List<SourceFolder> sourceFolders = new ArrayList<>();

            for (final ContentEntry entry : ModuleRootManager.getInstance(module).getContentEntries()) {
                for (final SourceFolder folder : entry.getSourceFolders()) {
                    final JavaSourceRootProperties properties = folder.getJpsElement().getProperties(JavaModuleSourceRootTypes.SOURCES);
                    if (properties != null && properties.isForGeneratedSources()) {
                        sourceFolders.add(folder);
                    }
                }
            }
            return sourceFolders;
        }

        return Collections.emptyList();
    }

    private Optional<VirtualFile> findFrameDirectory(final VirtualFile virtualFile) {
        VirtualFile frameDir = virtualFile;
        VirtualFile parserFrame = null;
        VirtualFile scannerFrame = null;
        do {
            frameDir = frameDir.getParent();
            if (frameDir != null) {
                parserFrame = frameDir.findChild("Parser.frame");
                scannerFrame = frameDir.findChild("Scanner.frame");
            }
        } while (parserFrame == null && scannerFrame == null && frameDir != null);

        if (parserFrame == null) {
            if (scannerFrame == null) {
                notify("Parser.frame and Scanner.frame file not found!", MessageType.ERROR);
            } else {
                notify("Parser.frame file not found!", MessageType.ERROR);
            }
            return Optional.empty();
        } else if (scannerFrame == null) {
            notify("Scanner.frame file not found!", MessageType.ERROR);
            return Optional.empty();
        }

        return Optional.of(frameDir);
    }

    private Optional<VirtualFile> generate(@NotNull CocoFile file, @NotNull AnActionEvent actionEvent) {
        final Optional<String> filePackage_ = CocoUtil.INSTANCE.getTargetPackage(file);

        Optional<VirtualFile> outDirFile_ = getGeneratedSourceFolders(file)
                .stream()
                .map(SourceFolder::getFile)
                .findFirst();

        if (!outDirFile_.isPresent()) {
            outDirFile_ = JavaProjectRootsUtil.getSuitableDestinationSourceRoots(file.getProject())
                    .stream()
                    .findFirst();
        }

        if (outDirFile_.isPresent() && filePackage_.isPresent()) {
            try {
                outDirFile_ = Optional.of(VfsUtil.createDirectoryIfMissing(outDirFile_.get(), filePackage_.get().replace(".", "/")));
            } catch (IOException ignore) {
            }
        }

        final VirtualFile parent = file.getVirtualFile().getParent();
        final Optional<VirtualFile> frameDirectory_ = findFrameDirectory(file.getVirtualFile());

        if (!frameDirectory_.isPresent()) {
            return Optional.empty();
        }


        String path = parent.getPath();
        String filePath = file.getVirtualFile().getPath();

        Scanner scanner = new Scanner(filePath);
        Parser parser = new Parser(scanner);

        final IntellijErrors intellijErrors = new IntellijErrors(parser);
        parser.errors = intellijErrors;

        parser.trace = new Trace(path);
        parser.tab = new Tab(parser);
        parser.dfa = new DFA(parser);
        parser.pgen = new ParserGen(parser);


        parser.tab.srcName = filePath;
        parser.tab.srcDir = path;
        filePackage_.ifPresent(s -> parser.tab.nsName = s);
        parser.tab.frameDir = frameDirectory_.get().getPath();
        parser.tab.outDir = outDirFile_.map(VirtualFile::getPath).orElse(path);

        try {
            parser.Parse();
        } catch (RuntimeException e) {
            notify(e.getMessage(), MessageType.ERROR);
            return Optional.empty();
        }

        parser.trace.Close();

        intellijErrors.getErrors()
                .forEach(notification -> notify(file.getVirtualFile().getName() + ": " + notification.getContent(), MessageType.ERROR, true));

        intellijErrors.getWarnings()
                .forEach(notification -> notify(file.getVirtualFile().getName() + ": " + notification.getContent(), MessageType.WARNING, true));


        if (intellijErrors.getErrorCount() != 0) {
            notify("Coco/R Compiler completed with " + intellijErrors.getErrorCount() + " errors", MessageType.ERROR);
        } else if (intellijErrors.getWarningCount() != 0) {
            notify("Coco/R Compiler completed with " + intellijErrors.getWarningCount() + " warnings", MessageType.WARNING);
        } else {
            notify("Coco/R Compiler completed successfully", MessageType.INFO);
        }

        final Project eventProject = getEventProject(actionEvent);
        SwingUtilities.invokeLater(() -> {

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

        return outDirFile_;
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
