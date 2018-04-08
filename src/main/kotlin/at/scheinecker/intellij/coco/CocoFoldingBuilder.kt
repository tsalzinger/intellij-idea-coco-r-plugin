package at.scheinecker.intellij.coco

import at.scheinecker.intellij.coco.psi.*
import com.intellij.lang.ASTNode
import com.intellij.lang.folding.FoldingBuilderEx
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil

class CocoFoldingBuilder : FoldingBuilderEx() {

    override fun buildFoldRegions(root: PsiElement, document: Document, quick: Boolean): Array<FoldingDescriptor> {
        return listOf(
                CocoImports::class.java,
                CocoDirectives::class.java,
                CocoGlobalFieldsAndMethods::class.java,
                CocoCharacters::class.java,
                CocoTokens::class.java,
                CocoTokenDecl::class.java,
                CocoPragmas::class.java,
                CocoPragmaDecl::class.java,
                CocoComments::class.java,
                CocoArbitraryStatements::class.java,
                CocoEmbeddedBooleanExpression::class.java,
                CocoEmbeddedImports::class.java,
                CocoEmbeddedStatements::class.java,
                CocoProduction::class.java,
                CocoEnd::class.java,
                CocoParserSpecification::class.java,
                PsiComment::class.java
        )
                .flatMap { PsiTreeUtil.findChildrenOfType(root, it) }
                .filter { containsNewline(it.text) }
                .mapNotNull {
                    FoldingDescriptor(it.node, TextRange.from(it.textOffset, it.text.trimEnd().length))
                }
                .toTypedArray()
    }

    override fun getPlaceholderText(node: ASTNode): String? {
        val psiElement = node.psi ?: return null

        return when (psiElement) {
            is CocoDirectives -> "DIRECTIVES (${psiElement.directiveList.size}) ..."
            is CocoImports -> "IMPORTS / INCLUDES ..."
            is CocoGlobalFieldsAndMethods -> "GLOBAL FIELDS AND METHODS ..."
            is CocoComments -> "COMMENTS (${psiElement.commentDeclList.size}) ..."
            is CocoCharacters -> "CHARACTERS (${psiElement.setDeclList.size}) ..."
            is CocoTokens -> "TOKENS (${psiElement.tokenDeclList.size}) ..."
            is CocoPragmas -> "PRAGMAS (${psiElement.pragmaDeclList.size}) ..."
            is CocoParserSpecification -> "PRODUCTIONS (${psiElement.productionList.size}) ..."
            is PsiComment -> "/* ... */"
            is CocoEnd -> psiElement.text.replace(Regex("\n|\r|\n\r"), "")
            is CocoNamedElement -> "${psiElement.name ?: "??"} ..."
            else -> "${node.elementType} ..."
        }
    }

    override fun isCollapsedByDefault(node: ASTNode): Boolean {
        return containsNewline(node.text)
    }

    private fun containsNewline(text: String): Boolean {
        val trimmed = text.trim('\n', '\r', '\t', ' ')
        return trimmed.contains('\n') || trimmed.contains('\r')
    }
}