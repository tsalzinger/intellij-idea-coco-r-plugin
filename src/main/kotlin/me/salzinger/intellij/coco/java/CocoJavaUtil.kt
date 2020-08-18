package me.salzinger.intellij.coco.java

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer
import com.intellij.codeInsight.daemon.impl.DaemonCodeAnalyzerImpl
import com.intellij.codeInsight.daemon.impl.DaemonProgressIndicator
import com.intellij.codeInsight.daemon.impl.HighlightInfo
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.progress.ProcessCanceledException
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.progress.util.AbstractProgressIndicatorExBase
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Computable
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.ex.ProgressIndicatorEx
import com.intellij.psi.JavaDirectoryService
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiTreeUtil
import me.salzinger.intellij.coco.psi.CocoFile
import java.util.Optional
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

/**
 * @author Thomas Salzinger [tsalzinger@gmail.com](mailto:tsalzinger@gmail.com)
 */
private val javaErrorCache: ConcurrentMap<PsiFile, List<HighlightInfo>> = ConcurrentHashMap()

object CocoJavaUtil {

    fun getDeclaredPackage(file: PsiFile): String? {
        return PsiTreeUtil.findChildOfType(
            file,
            me.salzinger.intellij.coco.psi.CocoDirectives::class.java
        )?.directiveList?.find {
            it.directiveName.text == "\$package"
        }?.directiveValue?.text
    }

    fun getTargetPackage(file: PsiFile): Optional<String> {
        if (file !is CocoFile) {
            return Optional.empty()
        }

        val declaredPackage = getDeclaredPackage(file)

        return Optional.ofNullable(declaredPackage ?: file.containingDirectory.toPackageName())
    }

    fun getParserClass(file: PsiFile): PsiClass? {
        val javaPsiFacade = ServiceManager.getService(file.project, JavaPsiFacade::class.java)

        val parserClassName = "${getTargetPackage(file).map { if (it.isEmpty()) it else "$it." }.orElse("")}Parser"
        return javaPsiFacade.findClass(parserClassName, GlobalSearchScope.allScope(javaPsiFacade.project))
    }

    fun getJavaErrors(file: PsiFile): List<HighlightInfo> {
        return javaErrorCache[file].orEmpty()
    }

    fun analyzeJavaErrors(cocoFile: CocoFile): List<HighlightInfo> {
        javaErrorCache.remove(cocoFile)

        val parserClass = getParserClass(cocoFile) ?: return emptyList()

        val project = parserClass.project

        val task = object : Task.WithResult<List<HighlightInfo>, Exception>(
            project,
            "Analyzing generated parser code",
            true
        ) {
            override fun compute(progress: ProgressIndicator): List<HighlightInfo> {
                if (progress.isCanceled) throw ProcessCanceledException()

                val file = parserClass.containingFile.virtualFile
                progress.text = "Processing ${file.presentableUrl}..."

                return findCodeSmells(project, file, progress)
            }
        }

        ProgressManager.getInstance().run(task)

        return task.result.also {
            javaErrorCache[cocoFile] = it
        }
    }

    private fun findCodeSmells(project: Project, file: VirtualFile, progress: ProgressIndicator): List<HighlightInfo> {
        val codeAnalyzer = DaemonCodeAnalyzer.getInstance(project) as DaemonCodeAnalyzerImpl
        val daemonIndicator = DaemonProgressIndicator()

        if (progress is ProgressIndicatorEx) {
            progress.addStateDelegate(object : AbstractProgressIndicatorExBase() {
                override fun cancel() {
                    super.cancel()
                    daemonIndicator.cancel()
                }
            })
        }

        return ProgressManager.getInstance().runProcess(
            Computable<List<HighlightInfo>> outer@{
                return@outer DumbService.getInstance(project).runReadActionInSmartMode(
                    Computable<List<HighlightInfo>> {
                        val psiFile = PsiManager.getInstance(project).findFile(file)
                        val document = FileDocumentManager.getInstance().getDocument(file)
                        if (psiFile == null || document == null) {
                            return@Computable emptyList()
                        }
                        return@Computable codeAnalyzer
                            .runMainPasses(psiFile, document, daemonIndicator)
                            .filter { it.severity == HighlightSeverity.ERROR }
                    }
                )
            },
            daemonIndicator
        )
    }

    private fun PsiDirectory?.toPackageName(): String? {
        return if (this == null) {
            null
        } else {
            JavaDirectoryService.getInstance().getPackage(this)?.qualifiedName
        }
    }
}
