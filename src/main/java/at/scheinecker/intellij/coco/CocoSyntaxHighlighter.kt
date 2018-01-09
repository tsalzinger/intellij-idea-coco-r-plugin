package at.scheinecker.intellij.coco

import at.scheinecker.intellij.coco.psi.CocoTypes
import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.psi.tree.IElementType

/**
 * @author Thomas Scheinecker [tscheinecker@gmail.com](mailto:tscheinecker@gmail.com)
 */
class CocoSyntaxHighlighter : SyntaxHighlighter {

    override fun getHighlightingLexer(): Lexer {
        return CocoLexerAdapter()
    }

    override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> {
        if (tokenType == CocoTypes.LINE_COMMENT) {
            return LINE_COMMENT_KEYS
        } else if (tokenType == CocoTypes.BLOCK_COMMENT) {
            return BLOCK_COMMENT_KEYS
        } else if (tokenType == CocoTypes.NUMBER) {
            return NUMBER_KEYS
        } else if (tokenType == CocoTypes.PAR_CLOSE || tokenType == CocoTypes.PAR_OPEN) {
            return PARENTHESES_KEYS
        } else if (tokenType == CocoTypes.BRACK_CLOSE || tokenType == CocoTypes.BRACK_OPEN) {
            return BRACKETS_KEYS
        } else if (tokenType == CocoTypes.CURL_CLOSE || tokenType == CocoTypes.CURL_OPEN) {
            return BRACES_KEYS
        } else if (tokenType == CocoTypes.STRING) {
            return STRING_KEYS
        } else if (tokenType == CocoTypes.CHAR) {
            return CHAR_KEYS
        } else if (tokenType == CocoTypes.SEM_ACTION) {
            return SEM_ACTION_KEYS
        } else if (tokenType == CocoTypes.IDENT) {
            return IDENTIFIER_KEYS
        } else if (tokenType == CocoTypes.TERMINATOR) {
            return TERMINATOR_KEYS
        } else if (tokenType == CocoTypes.PLUS
                || tokenType == CocoTypes.MINUS
                || tokenType == CocoTypes.ASSIGNMENT
                || tokenType == CocoTypes.PIPE
                || tokenType == CocoTypes.RANGE) {
            return OPERATION_SIGN_KEYS
        } else if (tokenType == CocoTypes.SEM_ACTION_START ||
                tokenType == CocoTypes.SEM_ACTION_END ||
                tokenType == CocoTypes.ATTRIBUTES_START ||
                tokenType == CocoTypes.ATTRIBUTES_END ||
                tokenType == CocoTypes.GREATER_THEN ||
                tokenType == CocoTypes.SMALLER_THEN) {
            return MARKUP_TAG_KEYS
        } else if (tokenType == CocoTypes.KEYWORD_ANY ||
                tokenType == CocoTypes.KEYWORD_CASE ||
                tokenType == CocoTypes.KEYWORD_CHARACTERS ||
                tokenType == CocoTypes.KEYWORD_COMPILER ||
                tokenType == CocoTypes.KEYWORD_COMMENTS ||
                tokenType == CocoTypes.KEYWORD_CONTEXT ||
                tokenType == CocoTypes.KEYWORD_END ||
                tokenType == CocoTypes.KEYWORD_FROM ||
                tokenType == CocoTypes.KEYWORD_IF ||
                tokenType == CocoTypes.KEYWORD_IGNORE ||
                tokenType == CocoTypes.KEYWORD_IGNORECASE ||
                tokenType == CocoTypes.KEYWORD_IMPORT ||
                tokenType == CocoTypes.KEYWORD_NESTED ||
                tokenType == CocoTypes.KEYWORD_OUT ||
                tokenType == CocoTypes.KEYWORD_PRAGMAS ||
                tokenType == CocoTypes.KEYWORD_PRODUCTIONS ||
                tokenType == CocoTypes.KEYWORD_SYNC ||
                tokenType == CocoTypes.KEYWORD_TO ||
                tokenType == CocoTypes.KEYWORD_TOKENS ||
                tokenType == CocoTypes.KEYWORD_WEAK) {
            return KEYWORD_KEYS
        }
        return emptyArray()
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
