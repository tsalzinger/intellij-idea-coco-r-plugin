package at.scheinecker.intellij.coco.action;

import Coco.*;
import Coco.Scanner;
import at.scheinecker.intellij.coco.CocoUtil;
import at.scheinecker.intellij.coco.psi.CocoFile;
import com.intellij.codeInsight.CodeSmellInfo;
import com.intellij.compiler.CompilerMessageImpl;
import com.intellij.compiler.ProblemsView;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.compiler.CompilerMessage;
import com.intellij.openapi.compiler.CompilerMessageCategory;
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
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.model.java.JavaModuleSourceRootTypes;
import org.jetbrains.jps.model.java.JavaSourceRootProperties;

import java.io.IOException;
import java.util.*;

/**
 * Created by Thomas on 29/12/2014.
 */
public class CocoRAction extends AnAction {
    public static final NotificationGroup COCO_NOTIFICATION_GROUP = NotificationGroup.balloonGroup("Coco/R Compiler");

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
        final UUID exeuctionId = UUID.randomUUID();

        for (CocoFile file : bnfFiles) {
            WriteAction
                    .compute(() -> generate(file, exeuctionId))
                    .ifPresent(context -> {
                        markFileTreeAsDirtyAndReload(context.getOutputDir());

                        PsiClass parserClass = CocoUtil.INSTANCE.getParserClass(file);
                        if (parserClass != null) {
                            final List<CodeSmellInfo> javaErrors = CocoUtil.INSTANCE.getJavaErrors(parserClass);

                            final List<CompilerMessage> compilerMessages = context.getCompilerMessages();
                            for (CodeSmellInfo javaError : javaErrors) {
                                compilerMessages.add(new CompilerMessageImpl(
                                                context.getProject(),
                                                CompilerMessageCategory.ERROR,
                                                javaError.getDescription(),
                                                parserClass.getContainingFile().getVirtualFile(),
                                                javaError.getStartLine() + 1,
                                                javaError.getStartColumn(),
                                                null
                                        )
                                );
                            }
                        }
                        showProblems(context);
                    });

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

    private Optional<CocoCompilerContext> generate(@NotNull CocoFile file, @NotNull final UUID executionId) {
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


        final String path = parent.getPath();
        final String outDir = outDirFile_.map(VirtualFile::getPath).orElse(path);
        final String filePath = file.getVirtualFile().getPath();

        final Scanner scanner = new Scanner(filePath);
        final Parser parser = new Parser(scanner);

        final CocoCompilerContext context = new CocoCompilerContext(file, outDirFile_.orElse(parent), executionId);

        parser.errors = new IntellijCocoErrorAdapter(context);

        parser.trace = new Trace(path);
        parser.tab = new Tab(parser);
        parser.dfa = new DFA(parser);
        parser.pgen = new ParserGen(parser);


        parser.tab.srcName = filePath;
        parser.tab.srcDir = path;
        filePackage_.ifPresent(s -> parser.tab.nsName = s);
        parser.tab.frameDir = frameDirectory_.get().getPath();
        parser.tab.outDir = outDir;

        parser.Parse();

        parser.trace.Close();

        return Optional.of(context);
    }

    private void showProblems(final CocoCompilerContext context) {
        final Project project = context.getProject();
        if (project != null && !project.isDisposed()) {
            final ProblemsView view = ProblemsView.SERVICE.getInstance(project);
            final UUID executionId = context.getExecutionId();

            view.clearOldMessages(null, executionId);

            final List<CompilerMessage> compilerMessages = context.getCompilerMessages();

            compilerMessages
                    .forEach(message -> view.addMessage(message, executionId));

            final VirtualFile inputFile = context.getInputFile();
            String statusMessage = "Coco/R Compiler for '" + inputFile.getName() + "'  completed";
            CompilerMessageCategory category = CompilerMessageCategory.INFORMATION;

            if (context.getErrorsCount() != 0) {
                statusMessage += " with " + context.getErrorsCount() + " errors and " + context.getWarningsCount() + " warnings";
                category = CompilerMessageCategory.ERROR;
            } else if (context.getWarningsCount() != 0) {
                statusMessage += " with " + context.getWarningsCount() + " warnings";
                category = CompilerMessageCategory.WARNING;
            } else {
                statusMessage += " successfully";
            }

            view.addMessage(new CompilerMessageImpl(project, category, statusMessage), executionId);

            if (category != CompilerMessageCategory.INFORMATION) {
                ToolWindowManager.getInstance(context.getProject()).getToolWindow("Problems").activate(null);
            }
        }
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
