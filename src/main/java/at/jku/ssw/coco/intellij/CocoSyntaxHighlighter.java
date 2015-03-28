package at.jku.ssw.coco.intellij;

import at.jku.ssw.coco.intellij.psi.CocoTypes;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Scheinecker <a href="mailto:tscheinecker@gmail.com">tscheinecker@gmail.com</a>
 */
public class CocoSyntaxHighlighter extends SyntaxHighlighterBase {
    public static final TextAttributesKey LINE_COMMENT = TextAttributesKey.createTextAttributesKey("LINE_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);
    private static final TextAttributesKey[] LINE_COMMENT_KEYS = new TextAttributesKey[]{LINE_COMMENT};
    public static final TextAttributesKey BLOCK_COMMENT = TextAttributesKey.createTextAttributesKey("BLOCK_COMMENT", DefaultLanguageHighlighterColors.BLOCK_COMMENT);
    private static final TextAttributesKey[] BLOCK_COMMENT_KEYS = new TextAttributesKey[]{BLOCK_COMMENT};
    public static final TextAttributesKey KEYWORD = TextAttributesKey.createTextAttributesKey("KEYWORD", DefaultLanguageHighlighterColors.KEYWORD);
    private static final TextAttributesKey[] KEYWORD_KEYS = new TextAttributesKey[]{KEYWORD};
    public static final TextAttributesKey STRING = TextAttributesKey.createTextAttributesKey("STRING", DefaultLanguageHighlighterColors.STRING);
    private static final TextAttributesKey[] STRING_KEYS = new TextAttributesKey[]{STRING};
    public static final TextAttributesKey CHAR = TextAttributesKey.createTextAttributesKey("CHAR", DefaultLanguageHighlighterColors.STRING);
    private static final TextAttributesKey[] CHAR_KEYS = new TextAttributesKey[]{CHAR};
    public static final TextAttributesKey NUMBER = TextAttributesKey.createTextAttributesKey("NUMBER", DefaultLanguageHighlighterColors.NUMBER);
    private static final TextAttributesKey[] NUMBER_KEYS = new TextAttributesKey[]{NUMBER};
    public static final TextAttributesKey MARKUP_TAG = TextAttributesKey.createTextAttributesKey("MARKUP_TAG", DefaultLanguageHighlighterColors.MARKUP_TAG);
    private static final TextAttributesKey[] MARKUP_TAG_KEYS = new TextAttributesKey[]{MARKUP_TAG};
    public static final TextAttributesKey SEM_ACTION = TextAttributesKey.createTextAttributesKey("SEM_ACTION", DefaultLanguageHighlighterColors.TEMPLATE_LANGUAGE_COLOR);
    private static final TextAttributesKey[] SEM_ACTION_KEYS = new TextAttributesKey[]{SEM_ACTION};
    public static final TextAttributesKey IDENTIFIER = TextAttributesKey.createTextAttributesKey("IDENTIFIER", DefaultLanguageHighlighterColors.IDENTIFIER);
    private static final TextAttributesKey[] IDENTIFIER_KEYS = new TextAttributesKey[]{IDENTIFIER};
    public static final TextAttributesKey TERMINATOR = TextAttributesKey.createTextAttributesKey("TERMINATOR", DefaultLanguageHighlighterColors.DOT);
    private static final TextAttributesKey[] TERMINATOR_KEYS = new TextAttributesKey[]{TERMINATOR};
    public static final TextAttributesKey OPERATION_SIGN = TextAttributesKey.createTextAttributesKey("OPERATIONS_SIGN", DefaultLanguageHighlighterColors.OPERATION_SIGN);
    private static final TextAttributesKey[] OPERATION_SIGN_KEYS = new TextAttributesKey[]{OPERATION_SIGN};
    public static final TextAttributesKey PARENTHESES = TextAttributesKey.createTextAttributesKey("PARENTHESES", DefaultLanguageHighlighterColors.PARENTHESES);
    private static final TextAttributesKey[] PARENTHESES_KEYS = new TextAttributesKey[]{PARENTHESES};
    public static final TextAttributesKey BRACKETS = TextAttributesKey.createTextAttributesKey("BRACKETS", DefaultLanguageHighlighterColors.BRACKETS);
    private static final TextAttributesKey[] BRACKETS_KEYS = new TextAttributesKey[]{BRACKETS};
    public static final TextAttributesKey BRACES = TextAttributesKey.createTextAttributesKey("BRACES", DefaultLanguageHighlighterColors.BRACES);
    private static final TextAttributesKey[] BRACES_KEYS = new TextAttributesKey[]{BRACES};

    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return new CocoLexerAdapter();
    }

    @NotNull
    @Override
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
        if (tokenType.equals(CocoTypes.LINE_COMMENT)) {
            return LINE_COMMENT_KEYS;
        } else if (tokenType.equals(CocoTypes.BLOCK_COMMENT)) {
            return BLOCK_COMMENT_KEYS;
        }  else if (tokenType.equals(CocoTypes.NUMBER)) {
            return NUMBER_KEYS;
        } else if (tokenType.equals(CocoTypes.PAR_CLOSE) ||
                tokenType.equals(CocoTypes.PAR_OPEN)) {
            return PARENTHESES_KEYS;
        } else if (tokenType.equals(CocoTypes.BRACK_CLOSE) ||
                tokenType.equals(CocoTypes.BRACK_OPEN)) {
            return BRACKETS_KEYS;
        } else if (tokenType.equals(CocoTypes.CURL_CLOSE) ||
                tokenType.equals(CocoTypes.CURL_OPEN)) {
            return BRACES_KEYS;
        } else if (tokenType.equals(CocoTypes.STRING)) {
            return STRING_KEYS;
        } else if (tokenType.equals(CocoTypes.CHAR)) {
            return CHAR_KEYS;
        } else if (tokenType.equals(CocoTypes.SEM_ACTION)) {
            return SEM_ACTION_KEYS;
        } else if (tokenType.equals(CocoTypes.IDENT)) {
            return IDENTIFIER_KEYS;
        } else if (tokenType.equals(CocoTypes.TERMINATOR)) {
            return TERMINATOR_KEYS;
        } else if (tokenType.equals(CocoTypes.PLUS)
                || tokenType.equals(CocoTypes.MINUS)
                || tokenType.equals(CocoTypes.ASSIGNMENT)
                || tokenType.equals(CocoTypes.PIPE)
                || tokenType.equals(CocoTypes.RANGE)) {
            return OPERATION_SIGN_KEYS;
        } else if (
                tokenType.equals(CocoTypes.SEM_ACTION_START) ||
                tokenType.equals(CocoTypes.SEM_ACTION_END) ||
                tokenType.equals(CocoTypes.GREATER_THEN) ||
                tokenType.equals(CocoTypes.SMALLER_THEN)) {
            return MARKUP_TAG_KEYS;
        } else if (tokenType.equals(CocoTypes.KEYWORD_ANY) ||
                tokenType.equals(CocoTypes.KEYWORD_CHARACTERS) ||
                tokenType.equals(CocoTypes.KEYWORD_COMPILER) ||
                tokenType.equals(CocoTypes.KEYWORD_COMMENTS) ||
                tokenType.equals(CocoTypes.KEYWORD_CONTEXT) ||
                tokenType.equals(CocoTypes.KEYWORD_END) ||
                tokenType.equals(CocoTypes.KEYWORD_FROM) ||
                tokenType.equals(CocoTypes.KEYWORD_IF) ||
                tokenType.equals(CocoTypes.KEYWORD_IGNORE) ||
                tokenType.equals(CocoTypes.KEYWORD_IGNORECASE) ||
                tokenType.equals(CocoTypes.KEYWORD_IMPORT) ||
                tokenType.equals(CocoTypes.KEYWORD_NESTED) ||
                tokenType.equals(CocoTypes.KEYWORD_OUT) ||
                tokenType.equals(CocoTypes.KEYWORD_PRAGMAS) ||
                tokenType.equals(CocoTypes.KEYWORD_PRODUCTIONS) ||
                tokenType.equals(CocoTypes.KEYWORD_SYNC) ||
                tokenType.equals(CocoTypes.KEYWORD_TO) ||
                tokenType.equals(CocoTypes.KEYWORD_TOKENS) ||
                tokenType.equals(CocoTypes.KEYWORD_WEAK)) {
            return KEYWORD_KEYS;
        }
        return new TextAttributesKey[0];
    }
}
