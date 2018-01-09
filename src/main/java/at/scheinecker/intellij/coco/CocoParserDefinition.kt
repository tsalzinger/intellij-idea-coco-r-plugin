package at.scheinecker.intellij.coco

import at.scheinecker.intellij.coco.parser.CocoParser
import at.scheinecker.intellij.coco.psi.CocoFile
import at.scheinecker.intellij.coco.psi.CocoTypes
import com.intellij.lang.ASTNode
import com.intellij.lang.Language
import com.intellij.lang.ParserDefinition
import com.intellij.lang.PsiParser
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet

/**
 * @author Thomas Scheinecker [tscheinecker@gmail.com](mailto:tscheinecker@gmail.com)
 */
class CocoParserDefinition : ParserDefinition {

    override fun createLexer(project: Project): Lexer {
        return CocoLexerAdapter()
    }

    override fun createParser(project: Project): PsiParser {
        return CocoParser()
    }

    override fun getFileNodeType(): IFileElementType {
        return FILE
    }

    override fun getWhitespaceTokens(): TokenSet {
        return WHITE_SPACES
    }

    override fun getCommentTokens(): TokenSet {
        return COMMENTS
    }

    override fun getStringLiteralElements(): TokenSet {
        return STRING
    }

    override fun createElement(node: ASTNode): PsiElement {
        return CocoTypes.Factory.createElement(node)
    }

    override fun createFile(viewProvider: FileViewProvider): PsiFile {
        return CocoFile(viewProvider)
    }

    override fun spaceExistanceTypeBetweenTokens(left: ASTNode, right: ASTNode): ParserDefinition.SpaceRequirements {
        return ParserDefinition.SpaceRequirements.MAY
    }

    companion object {
        val WHITE_SPACES = TokenSet.create(TokenType.WHITE_SPACE)
        val COMMENTS = TokenSet.create(CocoTypes.BLOCK_COMMENT, CocoTypes.LINE_COMMENT)
        val STRING = TokenSet.create(CocoTypes.STRING)

        val FILE = IFileElementType(Language.findInstance(CocoLanguage::class.java))
    }
}
