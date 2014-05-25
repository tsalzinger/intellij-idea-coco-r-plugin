package at.jku.ssw.coco.intellij;

import at.jku.ssw.coco.intellij.parser.CocoParser;
import at.jku.ssw.coco.intellij.psi.CocoFile;
import at.jku.ssw.coco.intellij.psi.CocoTypes;
import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Scheinecker <a href="mailto:tscheinecker@gmail.com">tscheinecker@gmail.com</a>
 */
public class CocoParserDefinition implements ParserDefinition {
    public static final TokenSet WHITE_SPACES = TokenSet.create(TokenType.WHITE_SPACE);
    //    public static final TokenSet COMMENTS = TokenSet.create(SimpleTypes.COMMENT);
    public static final TokenSet STRING = TokenSet.create(CocoTypes.STRING);

    public static final IFileElementType FILE = new IFileElementType(Language.findInstance(CocoLanguage.class));

    @NotNull
    @Override
    public Lexer createLexer(Project project) {
        return new CocoLexerAdapter();
    }

    @Override
    public PsiParser createParser(Project project) {
        return new CocoParser();
    }

    @Override
    public IFileElementType getFileNodeType() {
        return FILE;
    }

    @NotNull
    @Override
    public TokenSet getWhitespaceTokens() {
        return WHITE_SPACES;
    }

    @NotNull
    @Override
    public TokenSet getCommentTokens() {
        return TokenSet.EMPTY;
    }

    @NotNull
    @Override
    public TokenSet getStringLiteralElements() {
        return STRING;
    }

    @NotNull
    @Override
    public PsiElement createElement(ASTNode node) {
        return CocoTypes.Factory.createElement(node);
    }

    @Override
    public PsiFile createFile(FileViewProvider viewProvider) {
        return new CocoFile(viewProvider);
    }

    @Override
    public SpaceRequirements spaceExistanceTypeBetweenTokens(ASTNode left, ASTNode right) {
        return SpaceRequirements.MAY;
    }
}
