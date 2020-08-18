package me.salzinger.intellij.coco

import com.intellij.lang.ASTNode
import com.intellij.lang.folding.FoldingBuilderEx
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import me.salzinger.intellij.coco.psi.CocoCharacters
import me.salzinger.intellij.coco.psi.CocoComments
import me.salzinger.intellij.coco.psi.CocoDirectives
import me.salzinger.intellij.coco.psi.CocoEnd
import me.salzinger.intellij.coco.psi.CocoGlobalFieldsAndMethods
import me.salzinger.intellij.coco.psi.CocoImports
import me.salzinger.intellij.coco.psi.CocoNamedElement
import me.salzinger.intellij.coco.psi.CocoParserSpecification
import me.salzinger.intellij.coco.psi.CocoPragmas
import me.salzinger.intellij.coco.psi.CocoTokens

class CocoFoldingBuilder : FoldingBuilderEx() {

    override fun buildFoldRegions(root: PsiElement, document: Document, quick: Boolean): Array<FoldingDescriptor> {
        return listOf(
            CocoImports::class.java,
            CocoDirectives::class.java,
            CocoGlobalFieldsAndMethods::class.java,
            CocoCharacters::class.java,
            CocoTokens::class.java,
            me.salzinger.intellij.coco.psi.CocoTokenDecl::class.java,
            CocoPragmas::class.java,
            me.salzinger.intellij.coco.psi.CocoPragmaDecl::class.java,
            CocoComments::class.java,
            me.salzinger.intellij.coco.psi.CocoArbitraryStatements::class.java,
            me.salzinger.intellij.coco.psi.CocoEmbeddedBooleanExpression::class.java,
            me.salzinger.intellij.coco.psi.CocoEmbeddedImports::class.java,
            me.salzinger.intellij.coco.psi.CocoEmbeddedStatements::class.java,
            me.salzinger.intellij.coco.psi.CocoProduction::class.java,
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

    @Suppress("ComplexMethod")
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
