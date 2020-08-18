@file:Suppress("TooManyFunctions")

package me.salzinger.intellij.coco

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiNameIdentifierOwner
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiTreeUtil
import me.salzinger.intellij.coco.psi.CocoCocoInjectorHost
import me.salzinger.intellij.coco.psi.CocoCompiler
import me.salzinger.intellij.coco.psi.CocoGlobalFieldsAndMethods
import me.salzinger.intellij.coco.psi.CocoNamedElement
import me.salzinger.intellij.coco.psi.CocoParserSpecification
import me.salzinger.intellij.coco.psi.CocoPragmaDecl
import me.salzinger.intellij.coco.psi.CocoProduction
import me.salzinger.intellij.coco.psi.CocoScannerSpecification
import me.salzinger.intellij.coco.psi.CocoSetDecl
import me.salzinger.intellij.coco.psi.CocoTokenDecl

fun findCompilers(file: PsiFile): List<CocoCompiler> {
    return PsiTreeUtil.findChildrenOfType(file, CocoCompiler::class.java).toList()
}

fun findParserSpecification(file: PsiFile): CocoParserSpecification? {
    return PsiTreeUtil.getChildOfType(
        file,
        CocoCocoInjectorHost::class.java
    )?.parserSpecification
}

fun findTokenDecls(file: PsiFile): List<CocoTokenDecl> {
    return findScannerSpecification(file)?.tokens?.tokenDeclList ?: emptyList()
}

fun findPragmaDecls(file: PsiFile): List<CocoPragmaDecl> {
    return findScannerSpecification(file)?.pragmas?.pragmaDeclList ?: emptyList()
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

fun findTokenDecls(project: Project?): List<CocoTokenDecl> {
    return getAllFiles(project)
        .flatMap { findTokenDecls(it) }
}

fun findCharacterDecls(project: Project?): List<CocoSetDecl> {
    return getAllFiles(project)
        .flatMap { findCharacterDeclarations(it) }
}

fun findProductions(project: Project?): List<CocoProduction> {
    return getAllFiles(project)
        .flatMap { findProductions(it) }
}

fun findGlobalFieldsAndMethods(file: PsiFile): CocoGlobalFieldsAndMethods? {
    return PsiTreeUtil.getChildOfType(
        file,
        CocoCocoInjectorHost::class.java
    )?.globalFieldsAndMethods
}

fun findScannerSpecification(file: PsiFile): CocoScannerSpecification? {
    return PsiTreeUtil.getChildOfType(
        file,
        CocoCocoInjectorHost::class.java
    )?.scannerSpecification
}

fun findProductions(file: PsiFile): List<CocoProduction> {
    return findParserSpecification(file)?.productionList ?: emptyList()
}

fun findCharacterDeclarations(file: PsiFile): List<CocoSetDecl> {
    return findScannerSpecification(file)?.characters?.setDeclList ?: emptyList()
}

fun <T : CocoNamedElement> List<T>.findByName(name: String?): T? {
    if (name.isNullOrBlank()) {
        return null
    }

    return filterByName(name).firstOrNull()
}

fun <T : PsiNameIdentifierOwner> List<T>.filterByName(name: String?): List<T> {
    return if (name.isNullOrBlank()) {
        this
    } else {
        filter { item -> name == item.name }
    }
}

private fun getAllFiles(project: Project?): List<PsiFile> {
    if (project == null) {
        return emptyList()
    }

    val psiManager = PsiManager.getInstance(project)

    return FileTypeIndex.getFiles(CocoFileType.INSTANCE, GlobalSearchScope.allScope(project))
        .mapNotNull(psiManager::findFile)
}
