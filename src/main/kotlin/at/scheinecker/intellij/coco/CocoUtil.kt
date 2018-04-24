package at.scheinecker.intellij.coco

import at.scheinecker.intellij.coco.psi.*
import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer
import com.intellij.codeInsight.daemon.impl.DaemonCodeAnalyzerImpl
import com.intellij.codeInsight.daemon.impl.DaemonProgressIndicator
import com.intellij.codeInsight.daemon.impl.HighlightInfo
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.progress.ProcessCanceledException
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.progress.util.AbstractProgressIndicatorExBase
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Computable
import com.intellij.openapi.util.Ref
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.ex.ProgressIndicatorEx
import com.intellij.psi.*
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.FileTypeIndexImpl
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.ExceptionUtil
import com.intellij.util.indexing.FileBasedIndex
import org.apache.commons.lang.StringUtils
import org.jetbrains.annotations.Contract
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

/**
 * @author Thomas Scheinecker [tscheinecker@gmail.com](mailto:tscheinecker@gmail.com)
 */
private val LOG = Logger.getInstance(CocoUtil::class.java)

private val javaErrorCache: ConcurrentMap<PsiFile, List<HighlightInfo>> = ConcurrentHashMap()

object CocoUtil {

    fun findCompilerNames(project: Project?): List<String> {
        return findCompilers(project)
                .map { it.name }
                .filterNotNull()
    }

    @Contract("_, null -> null")
    fun findCompiler(file: PsiFile, name: String?): CocoCompiler? {
        return findByName(findCompilers(file), name)
    }

    fun findCompilers(file: PsiFile): List<CocoCompiler> {
        return PsiTreeUtil.findChildrenOfType(file, CocoCompiler::class.java).toList()
    }

    fun findCompilers(project: Project?, name: String): List<CocoCompiler> {
        return findCompilers(project)
                .filter { compiler -> name == compiler.name }
    }

    fun findCompilers(project: Project?): List<CocoCompiler> {
        if (project == null) {
            return emptyList()
        }

        return FileTypeIndexImpl
                .getFiles(CocoFileType.INSTANCE, GlobalSearchScope.allScope(project))
                .mapNotNull { PsiManager.getInstance(project).findFile(it) as CocoFile? }
                .flatMap { PsiTreeUtil.getChildrenOfType(it, CocoCompiler::class.java)?.asList() ?: emptyList() }
    }

    fun findCharacterDeclarations(file: PsiFile): List<CocoSetDecl> {
        val scannerSpecification = findScannerSpecification(file) ?: return emptyList()

        val characters = scannerSpecification.characters ?: return emptyList()

        return characters.setDeclList
    }

    @Contract("_, null -> null")
    fun findCharacterDeclaration(file: PsiFile, name: String?): CocoSetDecl? {
        return findByName(findCharacterDeclarations(file), name)
    }

    fun getDeclaredPackage(file: PsiFile): String? {
        return PsiTreeUtil.findChildOfType(file, CocoDirectives::class.java)?.directiveList?.find {
            it.directiveName.text == "\$package"
        }?.directiveValue?.text
    }

    fun getTargetPackage(file: PsiFile): Optional<String> {
        if (file !is CocoFile) {
            return Optional.empty()
        }
        val declaredPackage = getDeclaredPackage(file)
        if (declaredPackage != null) {
            return Optional.of(declaredPackage)
        }

        val containingDirectory = file.containingDirectory ?: return Optional.empty()

        return Optional.ofNullable(JavaDirectoryService.getInstance().getPackage(containingDirectory)?.qualifiedName)
    }

    fun getParserClass(file: PsiFile): PsiClass? {
        val javaPsiFacade = ServiceManager.getService(file.project, JavaPsiFacade::class.java)

        val parserClassName = "${getTargetPackage(file).map { if (it.isEmpty()) it else "${it}." }.orElse("")}Parser"
        return javaPsiFacade.findClass(parserClassName, GlobalSearchScope.allScope(javaPsiFacade.project))
    }

    fun getJavaErrors(file: PsiFile): List<HighlightInfo> {
        return javaErrorCache[file].orEmpty()
    }

    fun analyzeJavaErrors(cocoFile: CocoFile): List<HighlightInfo> {
        javaErrorCache.remove(cocoFile)

        val parserClass = getParserClass(cocoFile) ?: return emptyList()

        val project = parserClass.project

        val exception = Ref.create<Exception>()
        val results = mutableListOf<HighlightInfo>()

        ProgressManager.getInstance().run(object : Task.Modal(project, "Analyzing generated parser code", true) {
            override fun run(progress: ProgressIndicator) {
                try {
                    if (progress.isCanceled) throw ProcessCanceledException()

                    val file = parserClass.containingFile.virtualFile
                    progress.text = "Processing ${file.presentableUrl}..."
                    progress.fraction = 1.0

                    results.addAll(findCodeSmells(project, file, progress))
                } catch (e: ProcessCanceledException) {
                    exception.set(e)
                } catch (e: Exception) {
                    LOG.error(e)
                    exception.set(e)
                }

            }
        })

        if (!exception.isNull) {
            ExceptionUtil.rethrowAllAsUnchecked(exception.get())
        }

        javaErrorCache[cocoFile] = results
        return results
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

        return ProgressManager.getInstance().runProcess(Computable<List<HighlightInfo>> outer@{
            return@outer DumbService.getInstance(project).runReadActionInSmartMode(Computable<List<HighlightInfo>> {
                val psiFile = PsiManager.getInstance(project).findFile(file)
                val document = FileDocumentManager.getInstance().getDocument(file)
                if (psiFile == null || document == null) {
                    return@Computable emptyList()
                }
                return@Computable codeAnalyzer
                        .runMainPasses(psiFile, document, daemonIndicator)
                        .filter { it.severity == HighlightSeverity.ERROR }
            })

        }, daemonIndicator)
    }


    fun findProductions(file: PsiFile): List<CocoProduction> {
        val parserSpecification = findParserSpecification(file) ?: return emptyList()

        return parserSpecification.productionList
    }

    fun findScannerSpecification(file: PsiFile): CocoScannerSpecification? {
        return PsiTreeUtil.getChildOfType(file, CocoCocoInjectorHost::class.java)?.scannerSpecification
    }

    fun findParserSpecification(file: PsiFile): CocoParserSpecification? {
        return PsiTreeUtil.getChildOfType(file, CocoCocoInjectorHost::class.java)?.parserSpecification
    }

    @Contract("_, null -> null")
    fun findProduction(file: PsiFile, name: String?): CocoProduction? {
        return findByName(findProductions(file), name)
    }

    fun findTokenDecls(file: PsiFile): List<CocoTokenDecl> {
        val scannerSpecification = findScannerSpecification(file) ?: return emptyList()

        val tokens = scannerSpecification.tokens ?: return emptyList()

        return tokens.tokenDeclList
    }

    fun findPragmaDecls(file: PsiFile): List<CocoPragmaDecl> {
        val scannerSpecification = findScannerSpecification(file) ?: return emptyList()

        val pragmas = scannerSpecification.pragmas ?: return emptyList()

        return pragmas.pragmaDeclList
    }

    fun findNearestCocoNamedElement(element: PsiElement): CocoNamedElement {
        var searchElement: PsiElement? = element
        while (searchElement != null) {
            if (searchElement is CocoNamedElement) {
                return searchElement
            }
            searchElement = searchElement.parent
        }

        throw NullPointerException("Couldn't find CocoNamedElement for element $element")
    }

    fun findProductions(project: Project?): List<CocoProduction> {
        val characterDecls = ArrayList<CocoProduction>()
        val allFiles = getAllFiles(project)
        for (file in allFiles) {
            characterDecls.addAll(findProductions(file))
        }
        return characterDecls
    }

    fun findCharacterDecls(project: Project?): List<CocoSetDecl> {
        val characterDecls = ArrayList<CocoSetDecl>()
        val allFiles = getAllFiles(project)
        for (file in allFiles) {
            characterDecls.addAll(findCharacterDeclarations(file))
        }
        return characterDecls
    }

    fun findTokenDecls(project: Project?): List<CocoTokenDecl> {
        val tokenDecls = ArrayList<CocoTokenDecl>()
        val allFiles = getAllFiles(project)
        for (file in allFiles) {
            tokenDecls.addAll(findTokenDecls(file))
        }
        return tokenDecls
    }

    private fun getAllFiles(project: Project?): List<PsiFile> {
        if (project == null) {
            return emptyList()
        }
        val virtualFiles = FileBasedIndex.getInstance().getContainingFiles<FileType, Void>(FileTypeIndex.NAME, CocoFileType.INSTANCE, GlobalSearchScope.allScope(project))

        val psiFiles = ArrayList<PsiFile>()
        val psiManager = PsiManager.getInstance(project)

        for (virtualFile in virtualFiles) {
            val file = psiManager.findFile(virtualFile)
            if (file != null) {
                psiFiles.add(file)
            }
        }

        return psiFiles
    }

    fun findTokenDecls(project: Project?, name: String?): List<CocoTokenDecl> {
        return findAllByName(findTokenDecls(project), name)
    }

    fun findCharacterDecls(project: Project?, name: String?): List<CocoSetDecl> {
        return findAllByName(findCharacterDecls(project), name)
    }

    fun findProductions(project: Project?, name: String?): List<CocoProduction> {
        return findAllByName(findProductions(project), name)
    }

    @Contract("_, null -> null")
    fun findTokenDecl(file: PsiFile, name: String?): CocoTokenDecl? {
        return findByName(findTokenDecls(file), name)
    }

    @Contract("_, null -> null")
    private fun <T : CocoNamedElement> findByName(collection: List<T>, name: String?): T? {
        if (StringUtils.isBlank(name)) {
            return null
        }

        return findAllByName(collection, name).firstOrNull()
    }

    private fun <T : PsiNameIdentifierOwner> findAllByName(collection: List<T>, name: String?): List<T> {
        return if (StringUtils.isBlank(name)) {
            collection
        } else collection
                .filter { item -> name == item.name }

    }

    fun findGlobalFieldsAndMethods(file: PsiFile): CocoGlobalFieldsAndMethods? {
        return PsiTreeUtil.getChildOfType(file, CocoCocoInjectorHost::class.java)?.globalFieldsAndMethods
    }
}
