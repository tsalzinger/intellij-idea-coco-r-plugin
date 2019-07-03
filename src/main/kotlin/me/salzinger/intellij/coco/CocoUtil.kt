package me.salzinger.intellij.coco

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiNameIdentifierOwner
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiTreeUtil
import me.salzinger.intellij.coco.psi.CocoNamedElement
import org.apache.commons.lang.StringUtils
import org.jetbrains.annotations.Contract
import java.util.*

/**
 * @author Thomas Scheinecker [tscheinecker@gmail.com](mailto:tscheinecker@gmail.com)
 */
object CocoUtil {

    @Contract("_, null -> null")
    fun findCompiler(file: PsiFile, name: String?): me.salzinger.intellij.coco.psi.CocoCompiler? {
        return findByName(findCompilers(file), name)
    }

    fun findCompilers(file: PsiFile): List<me.salzinger.intellij.coco.psi.CocoCompiler> {
        return PsiTreeUtil.findChildrenOfType(file, me.salzinger.intellij.coco.psi.CocoCompiler::class.java).toList()
    }

    fun findCharacterDeclarations(file: PsiFile): List<me.salzinger.intellij.coco.psi.CocoSetDecl> {
        val scannerSpecification = findScannerSpecification(file) ?: return emptyList()

        val characters = scannerSpecification.characters ?: return emptyList()

        return characters.setDeclList
    }

    @Contract("_, null -> null")
    fun findCharacterDeclaration(file: PsiFile, name: String?): me.salzinger.intellij.coco.psi.CocoSetDecl? {
        return findByName(findCharacterDeclarations(file), name)
    }

    fun findProductions(file: PsiFile): List<me.salzinger.intellij.coco.psi.CocoProduction> {
        val parserSpecification = findParserSpecification(file) ?: return emptyList()

        return parserSpecification.productionList
    }

    fun findScannerSpecification(file: PsiFile): me.salzinger.intellij.coco.psi.CocoScannerSpecification? {
        return PsiTreeUtil.getChildOfType(file, me.salzinger.intellij.coco.psi.CocoCocoInjectorHost::class.java)?.scannerSpecification
    }

    fun findParserSpecification(file: PsiFile): me.salzinger.intellij.coco.psi.CocoParserSpecification? {
        return PsiTreeUtil.getChildOfType(file, me.salzinger.intellij.coco.psi.CocoCocoInjectorHost::class.java)?.parserSpecification
    }

    @Contract("_, null -> null")
    fun findProduction(file: PsiFile, name: String?): me.salzinger.intellij.coco.psi.CocoProduction? {
        return findByName(findProductions(file), name)
    }

    fun findTokenDecls(file: PsiFile): List<me.salzinger.intellij.coco.psi.CocoTokenDecl> {
        val scannerSpecification = findScannerSpecification(file) ?: return emptyList()

        val tokens = scannerSpecification.tokens ?: return emptyList()

        return tokens.tokenDeclList
    }

    fun findPragmaDecls(file: PsiFile): List<me.salzinger.intellij.coco.psi.CocoPragmaDecl> {
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

    fun findProductions(project: Project?): List<me.salzinger.intellij.coco.psi.CocoProduction> {
        val characterDecls = ArrayList<me.salzinger.intellij.coco.psi.CocoProduction>()
        val allFiles = getAllFiles(project)
        for (file in allFiles) {
            characterDecls.addAll(findProductions(file))
        }
        return characterDecls
    }

    fun findCharacterDecls(project: Project?): List<me.salzinger.intellij.coco.psi.CocoSetDecl> {
        val characterDecls = ArrayList<me.salzinger.intellij.coco.psi.CocoSetDecl>()
        val allFiles = getAllFiles(project)
        for (file in allFiles) {
            characterDecls.addAll(findCharacterDeclarations(file))
        }
        return characterDecls
    }

    fun findTokenDecls(project: Project?): List<me.salzinger.intellij.coco.psi.CocoTokenDecl> {
        val tokenDecls = ArrayList<me.salzinger.intellij.coco.psi.CocoTokenDecl>()
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

        val psiManager = PsiManager.getInstance(project)

        return FileTypeIndex.getFiles(CocoFileType.INSTANCE, GlobalSearchScope.allScope(project))
                .mapNotNull(psiManager::findFile)
    }

    fun findTokenDecls(project: Project?, name: String?): List<me.salzinger.intellij.coco.psi.CocoTokenDecl> {
        return findAllByName(findTokenDecls(project), name)
    }

    fun findCharacterDecls(project: Project?, name: String?): List<me.salzinger.intellij.coco.psi.CocoSetDecl> {
        return findAllByName(findCharacterDecls(project), name)
    }

    fun findProductions(project: Project?, name: String?): List<me.salzinger.intellij.coco.psi.CocoProduction> {
        return findAllByName(findProductions(project), name)
    }

    @Contract("_, null -> null")
    fun findTokenDecl(file: PsiFile, name: String?): me.salzinger.intellij.coco.psi.CocoTokenDecl? {
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

    fun findGlobalFieldsAndMethods(file: PsiFile): me.salzinger.intellij.coco.psi.CocoGlobalFieldsAndMethods? {
        return PsiTreeUtil.getChildOfType(file, me.salzinger.intellij.coco.psi.CocoCocoInjectorHost::class.java)?.globalFieldsAndMethods
    }
}
