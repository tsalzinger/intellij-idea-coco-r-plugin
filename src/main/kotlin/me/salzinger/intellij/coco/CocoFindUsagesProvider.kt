package me.salzinger.intellij.coco

import com.intellij.lang.cacheBuilder.DefaultWordsScanner
import com.intellij.lang.cacheBuilder.WordsScanner
import com.intellij.lang.findUsages.FindUsagesProvider
import com.intellij.lexer.FlexAdapter
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.TokenSet
import me.salzinger.intellij.coco.psi.CocoNamedElement

/**
 * Created by Thomas on 28/03/2015.
 */
class CocoFindUsagesProvider : FindUsagesProvider {

    override fun getWordsScanner(): WordsScanner {
        return DefaultWordsScanner(
            FlexAdapter(me.salzinger.intellij.coco.CocoLexer()),
            TokenSet.create(me.salzinger.intellij.coco.psi.CocoTypes.IDENT),
            TokenSet.create(
                me.salzinger.intellij.coco.psi.CocoTypes.LINE_COMMENT,
                me.salzinger.intellij.coco.psi.CocoTypes.BLOCK_COMMENT
            ),
            TokenSet.EMPTY
        )
    }

    override fun canFindUsagesFor(psiElement: PsiElement): Boolean {
        return psiElement is CocoNamedElement
    }

    override fun getHelpId(psiElement: PsiElement): String? {
        return null
    }

    override fun getType(element: PsiElement): String {
        if (element is me.salzinger.intellij.coco.psi.CocoTokenDecl) {
            return "token"
        }
        if (element is me.salzinger.intellij.coco.psi.CocoProduction) {
            return "production"
        }
        if (element is me.salzinger.intellij.coco.psi.CocoSetDecl) {
            return "character"
        }
        if (element is me.salzinger.intellij.coco.psi.CocoPragmaDecl) {
            return "pragma"
        }
        return if (element is me.salzinger.intellij.coco.psi.CocoCompiler) {
            "compiler"
        } else ""
    }

    override fun getDescriptiveName(element: PsiElement): String {
        if (element is CocoNamedElement) {
            val name = element.name
            if (name != null) {
                return name
            }
        }

        return ""
    }

    override fun getNodeText(element: PsiElement, useFullName: Boolean): String {
        return element.text
    }
}
