package me.salzinger.intellij.coco

import com.intellij.lang.ASTNode
import com.intellij.lang.folding.FoldingBuilderEx
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import me.salzinger.intellij.coco.psi.CocoNamedElement

class CocoFoldingBuilder : FoldingBuilderEx() {

    override fun buildFoldRegions(root: PsiElement, document: Document, quick: Boolean): Array<FoldingDescriptor> {
        return listOf(
                me.salzinger.intellij.coco.psi.CocoImports::class.java,
                me.salzinger.intellij.coco.psi.CocoDirectives::class.java,
                me.salzinger.intellij.coco.psi.CocoGlobalFieldsAndMethods::class.java,
                me.salzinger.intellij.coco.psi.CocoCharacters::class.java,
                me.salzinger.intellij.coco.psi.CocoTokens::class.java,
                me.salzinger.intellij.coco.psi.CocoTokenDecl::class.java,
                me.salzinger.intellij.coco.psi.CocoPragmas::class.java,
                me.salzinger.intellij.coco.psi.CocoPragmaDecl::class.java,
                me.salzinger.intellij.coco.psi.CocoComments::class.java,
                me.salzinger.intellij.coco.psi.CocoArbitraryStatements::class.java,
                me.salzinger.intellij.coco.psi.CocoEmbeddedBooleanExpression::class.java,
                me.salzinger.intellij.coco.psi.CocoEmbeddedImports::class.java,
                me.salzinger.intellij.coco.psi.CocoEmbeddedStatements::class.java,
                me.salzinger.intellij.coco.psi.CocoProduction::class.java,
                me.salzinger.intellij.coco.psi.CocoEnd::class.java,
                me.salzinger.intellij.coco.psi.CocoParserSpecification::class.java,
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
            is me.salzinger.intellij.coco.psi.CocoDirectives -> "DIRECTIVES (${psiElement.directiveList.size}) ..."
            is me.salzinger.intellij.coco.psi.CocoImports -> "IMPORTS / INCLUDES ..."
            is me.salzinger.intellij.coco.psi.CocoGlobalFieldsAndMethods -> "GLOBAL FIELDS AND METHODS ..."
            is me.salzinger.intellij.coco.psi.CocoComments -> "COMMENTS (${psiElement.commentDeclList.size}) ..."
            is me.salzinger.intellij.coco.psi.CocoCharacters -> "CHARACTERS (${psiElement.setDeclList.size}) ..."
            is me.salzinger.intellij.coco.psi.CocoTokens -> "TOKENS (${psiElement.tokenDeclList.size}) ..."
            is me.salzinger.intellij.coco.psi.CocoPragmas -> "PRAGMAS (${psiElement.pragmaDeclList.size}) ..."
            is me.salzinger.intellij.coco.psi.CocoParserSpecification -> "PRODUCTIONS (${psiElement.productionList.size}) ..."
            is PsiComment -> "/* ... */"
            is me.salzinger.intellij.coco.psi.CocoEnd -> psiElement.text.replace(Regex("\n|\r|\n\r"), "")
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