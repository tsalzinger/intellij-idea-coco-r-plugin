package at.scheinecker.intellij.coco

import at.scheinecker.intellij.coco.psi.*
import com.intellij.lang.cacheBuilder.DefaultWordsScanner
import com.intellij.lang.cacheBuilder.WordsScanner
import com.intellij.lang.findUsages.FindUsagesProvider
import com.intellij.lexer.FlexAdapter
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.TokenSet

/**
 * Created by Thomas on 28/03/2015.
 */
class CocoFindUsagesProvider : FindUsagesProvider {

    override fun getWordsScanner(): WordsScanner {
        return DefaultWordsScanner(FlexAdapter(CocoLexer()),
                TokenSet.create(CocoTypes.IDENT),
                TokenSet.create(CocoTypes.LINE_COMMENT, CocoTypes.BLOCK_COMMENT),
                TokenSet.EMPTY)
    }

    override fun canFindUsagesFor(psiElement: PsiElement): Boolean {
        return psiElement is CocoNamedElement
    }

    override fun getHelpId(psiElement: PsiElement): String? {
        return null
    }

    override fun getType(element: PsiElement): String {
        if (element is CocoTokenDecl) {
            return "token"
        }
        if (element is CocoProduction) {
            return "production"
        }
        if (element is CocoSetDecl) {
            return "character"
        }
        if (element is CocoPragmaDecl) {
            return "pragma"
        }
        return if (element is CocoCompiler) {
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
