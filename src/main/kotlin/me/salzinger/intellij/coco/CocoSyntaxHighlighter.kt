package me.salzinger.intellij.coco

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.psi.tree.IElementType

/**
 * @author Thomas Salzinger [tsalzinger@gmail.com](mailto:tsalzinger@gmail.com)
 */
class CocoSyntaxHighlighter : SyntaxHighlighter {

    override fun getHighlightingLexer(): Lexer {
        return CocoLexerAdapter()
    }

    override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> {
        when (tokenType) {
            me.salzinger.intellij.coco.psi.CocoTypes.LINE_COMMENT -> return LINE_COMMENT_KEYS
            me.salzinger.intellij.coco.psi.CocoTypes.BLOCK_COMMENT -> return BLOCK_COMMENT_KEYS
            me.salzinger.intellij.coco.psi.CocoTypes.NUMBER -> return NUMBER_KEYS
            me.salzinger.intellij.coco.psi.CocoTypes.PAR_CLOSE,
            me.salzinger.intellij.coco.psi.CocoTypes.PAR_OPEN -> return PARENTHESES_KEYS
            me.salzinger.intellij.coco.psi.CocoTypes.BRACK_CLOSE,
            me.salzinger.intellij.coco.psi.CocoTypes.BRACK_OPEN -> return BRACKETS_KEYS
            me.salzinger.intellij.coco.psi.CocoTypes.CURL_CLOSE,
            me.salzinger.intellij.coco.psi.CocoTypes.CURL_OPEN -> return BRACES_KEYS
            me.salzinger.intellij.coco.psi.CocoTypes.STRING -> return STRING_KEYS
            me.salzinger.intellij.coco.psi.CocoTypes.CHAR -> return CHAR_KEYS
            me.salzinger.intellij.coco.psi.CocoTypes.SEM_ACTION -> return SEM_ACTION_KEYS
            me.salzinger.intellij.coco.psi.CocoTypes.DIRECTIVE_VALUE,
            me.salzinger.intellij.coco.psi.CocoTypes.IDENT -> return IDENTIFIER_KEYS
            me.salzinger.intellij.coco.psi.CocoTypes.TERMINATOR -> return TERMINATOR_KEYS
            me.salzinger.intellij.coco.psi.CocoTypes.PLUS,
            me.salzinger.intellij.coco.psi.CocoTypes.MINUS,
            me.salzinger.intellij.coco.psi.CocoTypes.ASSIGNMENT,
            me.salzinger.intellij.coco.psi.CocoTypes.PIPE,
            me.salzinger.intellij.coco.psi.CocoTypes.RANGE -> return OPERATION_SIGN_KEYS
            me.salzinger.intellij.coco.psi.CocoTypes.SEM_ACTION_START,
            me.salzinger.intellij.coco.psi.CocoTypes.SEM_ACTION_END,
            me.salzinger.intellij.coco.psi.CocoTypes.ATTRIBUTES_START,
            me.salzinger.intellij.coco.psi.CocoTypes.ATTRIBUTES_END,
            me.salzinger.intellij.coco.psi.CocoTypes.GREATER_THEN,
            me.salzinger.intellij.coco.psi.CocoTypes.SMALLER_THEN -> return MARKUP_TAG_KEYS
            me.salzinger.intellij.coco.psi.CocoTypes.KEYWORD_ANY,
            me.salzinger.intellij.coco.psi.CocoTypes.DIRECTIVE_NAME,
            me.salzinger.intellij.coco.psi.CocoTypes.KEYWORD_CASE,
            me.salzinger.intellij.coco.psi.CocoTypes.KEYWORD_CHARACTERS,
            me.salzinger.intellij.coco.psi.CocoTypes.KEYWORD_COMPILER,
            me.salzinger.intellij.coco.psi.CocoTypes.KEYWORD_COMMENTS,
            me.salzinger.intellij.coco.psi.CocoTypes.KEYWORD_CONTEXT,
            me.salzinger.intellij.coco.psi.CocoTypes.KEYWORD_END,
            me.salzinger.intellij.coco.psi.CocoTypes.KEYWORD_FROM,
            me.salzinger.intellij.coco.psi.CocoTypes.KEYWORD_IF,
            me.salzinger.intellij.coco.psi.CocoTypes.KEYWORD_IGNORE,
            me.salzinger.intellij.coco.psi.CocoTypes.KEYWORD_IGNORECASE,
            me.salzinger.intellij.coco.psi.CocoTypes.KEYWORD_NESTED,
            me.salzinger.intellij.coco.psi.CocoTypes.KEYWORD_OUT,
            me.salzinger.intellij.coco.psi.CocoTypes.KEYWORD_PRAGMAS,
            me.salzinger.intellij.coco.psi.CocoTypes.KEYWORD_PRODUCTIONS,
            me.salzinger.intellij.coco.psi.CocoTypes.KEYWORD_SYNC,
            me.salzinger.intellij.coco.psi.CocoTypes.KEYWORD_TO,
            me.salzinger.intellij.coco.psi.CocoTypes.KEYWORD_TOKENS,
            me.salzinger.intellij.coco.psi.CocoTypes.KEYWORD_WEAK -> return KEYWORD_KEYS
            else -> return emptyArray()
        }
    }

    companion object {
        val LINE_COMMENT = TextAttributesKey.createTextAttributesKey("LINE_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT)
        private val LINE_COMMENT_KEYS = arrayOf(LINE_COMMENT)
        val BLOCK_COMMENT = TextAttributesKey.createTextAttributesKey("BLOCK_COMMENT", DefaultLanguageHighlighterColors.BLOCK_COMMENT)
        private val BLOCK_COMMENT_KEYS = arrayOf(BLOCK_COMMENT)
        val KEYWORD = TextAttributesKey.createTextAttributesKey("KEYWORD", DefaultLanguageHighlighterColors.KEYWORD)
        private val KEYWORD_KEYS = arrayOf(KEYWORD)
        val STRING = TextAttributesKey.createTextAttributesKey("STRING", DefaultLanguageHighlighterColors.STRING)
        private val STRING_KEYS = arrayOf(STRING)
        val CHAR = TextAttributesKey.createTextAttributesKey("CHAR", DefaultLanguageHighlighterColors.STRING)
        private val CHAR_KEYS = arrayOf(CHAR)
        val NUMBER = TextAttributesKey.createTextAttributesKey("NUMBER", DefaultLanguageHighlighterColors.NUMBER)
        private val NUMBER_KEYS = arrayOf(NUMBER)
        val MARKUP_TAG = TextAttributesKey.createTextAttributesKey("MARKUP_TAG", DefaultLanguageHighlighterColors.MARKUP_TAG)
        private val MARKUP_TAG_KEYS = arrayOf(MARKUP_TAG)
        val SEM_ACTION = TextAttributesKey.createTextAttributesKey("SEM_ACTION", DefaultLanguageHighlighterColors.TEMPLATE_LANGUAGE_COLOR)
        private val SEM_ACTION_KEYS = arrayOf(SEM_ACTION)
        val IDENTIFIER = TextAttributesKey.createTextAttributesKey("IDENTIFIER", DefaultLanguageHighlighterColors.IDENTIFIER)
        private val IDENTIFIER_KEYS = arrayOf(IDENTIFIER)
        val TERMINATOR = TextAttributesKey.createTextAttributesKey("TERMINATOR", DefaultLanguageHighlighterColors.DOT)
        private val TERMINATOR_KEYS = arrayOf(TERMINATOR)
        val OPERATION_SIGN = TextAttributesKey.createTextAttributesKey("OPERATIONS_SIGN", DefaultLanguageHighlighterColors.OPERATION_SIGN)
        private val OPERATION_SIGN_KEYS = arrayOf(OPERATION_SIGN)
        val PARENTHESES = TextAttributesKey.createTextAttributesKey("PARENTHESES", DefaultLanguageHighlighterColors.PARENTHESES)
        private val PARENTHESES_KEYS = arrayOf(PARENTHESES)
        val BRACKETS = TextAttributesKey.createTextAttributesKey("BRACKETS", DefaultLanguageHighlighterColors.BRACKETS)
        private val BRACKETS_KEYS = arrayOf(BRACKETS)
        val BRACES = TextAttributesKey.createTextAttributesKey("BRACES", DefaultLanguageHighlighterColors.BRACES)
        private val BRACES_KEYS = arrayOf(BRACES)
    }
}
