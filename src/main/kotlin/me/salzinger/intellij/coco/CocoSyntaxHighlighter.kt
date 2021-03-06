package me.salzinger.intellij.coco

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.psi.tree.IElementType
import me.salzinger.intellij.coco.psi.CocoTypes
import me.salzinger.intellij.coco.psi.CocoTypes.LINE_COMMENT

/**
 * @author Thomas Salzinger [tsalzinger@gmail.com](mailto:tsalzinger@gmail.com)
 */
class CocoSyntaxHighlighter : SyntaxHighlighter {

    override fun getHighlightingLexer(): Lexer {
        return CocoLexerAdapter()
    }

    @Suppress("ComplexMethod")
    override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> {
        return when (tokenType) {
            CocoTypes.LINE_COMMENT -> LINE_COMMENT_KEYS
            CocoTypes.BLOCK_COMMENT -> BLOCK_COMMENT_KEYS
            CocoTypes.NUMBER -> NUMBER_KEYS
            CocoTypes.PAR_CLOSE,
            CocoTypes.PAR_OPEN -> PARENTHESES_KEYS
            CocoTypes.BRACK_CLOSE,
            CocoTypes.BRACK_OPEN -> BRACKETS_KEYS
            CocoTypes.CURL_CLOSE,
            CocoTypes.CURL_OPEN -> BRACES_KEYS
            CocoTypes.STRING -> STRING_KEYS
            CocoTypes.CHAR -> CHAR_KEYS
            CocoTypes.SEM_ACTION -> SEM_ACTION_KEYS
            CocoTypes.DIRECTIVE_VALUE,
            CocoTypes.IDENT -> IDENTIFIER_KEYS
            CocoTypes.TERMINATOR -> TERMINATOR_KEYS
            CocoTypes.PLUS,
            CocoTypes.MINUS,
            CocoTypes.ASSIGNMENT,
            CocoTypes.PIPE,
            CocoTypes.RANGE -> OPERATION_SIGN_KEYS
            CocoTypes.SEM_ACTION_START,
            CocoTypes.SEM_ACTION_END,
            CocoTypes.ATTRIBUTES_START,
            CocoTypes.ATTRIBUTES_END,
            CocoTypes.GREATER_THEN,
            CocoTypes.SMALLER_THEN -> MARKUP_TAG_KEYS
            CocoTypes.KEYWORD_ANY,
            CocoTypes.DIRECTIVE_NAME,
            CocoTypes.KEYWORD_CASE,
            CocoTypes.KEYWORD_CHARACTERS,
            CocoTypes.KEYWORD_COMPILER,
            CocoTypes.KEYWORD_COMMENTS,
            CocoTypes.KEYWORD_CONTEXT,
            CocoTypes.KEYWORD_END,
            CocoTypes.KEYWORD_FROM,
            CocoTypes.KEYWORD_IF,
            CocoTypes.KEYWORD_IGNORE,
            CocoTypes.KEYWORD_IGNORECASE,
            CocoTypes.KEYWORD_NESTED,
            CocoTypes.KEYWORD_OUT,
            CocoTypes.KEYWORD_PRAGMAS,
            CocoTypes.KEYWORD_PRODUCTIONS,
            CocoTypes.KEYWORD_SYNC,
            CocoTypes.KEYWORD_TO,
            CocoTypes.KEYWORD_TOKENS,
            CocoTypes.KEYWORD_WEAK -> KEYWORD_KEYS
            else -> emptyArray()
        }
    }

    companion object {
        val LINE_COMMENT =
            TextAttributesKey.createTextAttributesKey("LINE_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT)
        private val LINE_COMMENT_KEYS = arrayOf(LINE_COMMENT)
        val BLOCK_COMMENT =
            TextAttributesKey.createTextAttributesKey("BLOCK_COMMENT", DefaultLanguageHighlighterColors.BLOCK_COMMENT)
        private val BLOCK_COMMENT_KEYS = arrayOf(BLOCK_COMMENT)
        val KEYWORD = TextAttributesKey.createTextAttributesKey("KEYWORD", DefaultLanguageHighlighterColors.KEYWORD)
        private val KEYWORD_KEYS = arrayOf(KEYWORD)
        val STRING = TextAttributesKey.createTextAttributesKey("STRING", DefaultLanguageHighlighterColors.STRING)
        private val STRING_KEYS = arrayOf(STRING)
        val CHAR = TextAttributesKey.createTextAttributesKey("CHAR", DefaultLanguageHighlighterColors.STRING)
        private val CHAR_KEYS = arrayOf(CHAR)
        val NUMBER = TextAttributesKey.createTextAttributesKey("NUMBER", DefaultLanguageHighlighterColors.NUMBER)
        private val NUMBER_KEYS = arrayOf(NUMBER)
        val MARKUP_TAG =
            TextAttributesKey.createTextAttributesKey("MARKUP_TAG", DefaultLanguageHighlighterColors.MARKUP_TAG)
        private val MARKUP_TAG_KEYS = arrayOf(MARKUP_TAG)
        val SEM_ACTION = TextAttributesKey.createTextAttributesKey(
            "SEM_ACTION",
            DefaultLanguageHighlighterColors.TEMPLATE_LANGUAGE_COLOR
        )
        private val SEM_ACTION_KEYS = arrayOf(SEM_ACTION)
        val IDENTIFIER =
            TextAttributesKey.createTextAttributesKey("IDENTIFIER", DefaultLanguageHighlighterColors.IDENTIFIER)
        private val IDENTIFIER_KEYS = arrayOf(IDENTIFIER)
        val TERMINATOR = TextAttributesKey.createTextAttributesKey("TERMINATOR", DefaultLanguageHighlighterColors.DOT)
        private val TERMINATOR_KEYS = arrayOf(TERMINATOR)
        val OPERATION_SIGN = TextAttributesKey.createTextAttributesKey(
            "OPERATIONS_SIGN",
            DefaultLanguageHighlighterColors.OPERATION_SIGN
        )
        private val OPERATION_SIGN_KEYS = arrayOf(OPERATION_SIGN)
        val PARENTHESES =
            TextAttributesKey.createTextAttributesKey("PARENTHESES", DefaultLanguageHighlighterColors.PARENTHESES)
        private val PARENTHESES_KEYS = arrayOf(PARENTHESES)
        val BRACKETS = TextAttributesKey.createTextAttributesKey("BRACKETS", DefaultLanguageHighlighterColors.BRACKETS)
        private val BRACKETS_KEYS = arrayOf(BRACKETS)
        val BRACES = TextAttributesKey.createTextAttributesKey("BRACES", DefaultLanguageHighlighterColors.BRACES)
        private val BRACES_KEYS = arrayOf(BRACES)
    }
}
