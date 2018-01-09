package at.scheinecker.intellij.coco

import at.scheinecker.intellij.coco.psi.*
import com.intellij.lang.ASTNode
import com.intellij.lang.folding.FoldingBuilderEx
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import org.apache.commons.lang.StringUtils

class CocoFoldingBuilder : FoldingBuilderEx() {

    override fun buildFoldRegions(root: PsiElement, document: Document, quick: Boolean): Array<FoldingDescriptor> {

        return listOf(
                CocoImports::class.java,
                CocoGlobalFieldsAndMethods::class.java,
                CocoCharacters::class.java,
                CocoTokens::class.java,
                CocoTokenDecl::class.java,
                CocoPragmas::class.java,
                CocoPragmaDecl::class.java,
                CocoComments::class.java,
                CocoArbitraryStatements::class.java,
                CocoArbitraryText::class.java,
                CocoProduction::class.java,
                CocoEnd::class.java,
                CocoParserSpecification::class.java,
                PsiComment::class.java
        )
                .flatMap { PsiTreeUtil.findChildrenOfType(root, it) }
                .filter { StringUtils.isNotBlank(it.text) }
                .mapNotNull { FoldingDescriptor(it.node, it.textRange) }
                .toTypedArray()
    }

    override fun getPlaceholderText(node: ASTNode): String? {
        val psiElement = node.psi ?: return null

        val completeText = psiElement.text.trim { it <= ' ' }.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        if (psiElement is CocoImports) {
            return "JAVA IMPORTS ..."
        }

        if (psiElement is CocoGlobalFieldsAndMethods) {
            return "JAVA FIELDS AND METHODS ..."
        }

        if (psiElement is CocoComments) {
            val size = psiElement.commentDeclList.size
            return "COMMENTS ($size) ..."
        }

        if (psiElement is CocoCharacters) {
            val size = psiElement.setDeclList.size
            return "CHARACTERS ($size) ..."
        }

        if (psiElement is CocoTokens) {
            val size = psiElement.tokenDeclList.size
            return "TOKENS ($size) ..."
        }

        if (psiElement is CocoPragmas) {
            val size = psiElement.pragmaDeclList.size
            return "PRAGMAS ($size) ..."
        }

        if (psiElement is CocoParserSpecification) {
            val size = psiElement.productionList.size
            return "PRODUCTIONS ($size) ..."
        }

        if (psiElement is PsiComment) {
            return if (psiElement.text.startsWith("//")) {
                "// ..."
            } else "/* ... */"

        }

        if (psiElement is CocoEnd) {
            val ident = psiElement.nameIdentifier
            return if (ident != null) {
                "END " + ident.text + "."
            } else "END ??."

        }

        if (psiElement is CocoNamedElement) {
            val name = psiElement.name
            return name!! + " ..."
        }

        return if (completeText.size == 1) {
            completeText[0] + "..."
        } else "..."

    }

    override fun isCollapsedByDefault(node: ASTNode): Boolean {
        return node.text.contains("\n")
    }
}