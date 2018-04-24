package at.scheinecker.intellij.coco

import at.scheinecker.intellij.coco.psi.*
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiNameIdentifierOwner
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.indexing.FileBasedIndex
import org.apache.commons.lang.StringUtils
import org.jetbrains.annotations.Contract
import java.util.*

/**
 * @author Thomas Scheinecker [tscheinecker@gmail.com](mailto:tscheinecker@gmail.com)
 */
object CocoUtil {

    @Contract("_, null -> null")
    fun findCompiler(file: PsiFile, name: String?): CocoCompiler? {
        return findByName(findCompilers(file), name)
    }

    fun findCompilers(file: PsiFile): List<CocoCompiler> {
        return PsiTreeUtil.findChildrenOfType(file, CocoCompiler::class.java).toList()
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
