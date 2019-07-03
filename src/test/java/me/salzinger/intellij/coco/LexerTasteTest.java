package me.salzinger.intellij.coco;

import com.intellij.psi.tree.IElementType;
import me.salzinger.intellij.coco.psi.CocoTypes;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Thomas on 23/03/2015.
 */
public class LexerTasteTest extends AbstractLexerTest {


    @Test
    public void testTasteATG() throws IOException {
        final InputStream tasteInputStream = LexerTasteTest.class.getResourceAsStream("/me/salzinger/intellij/coco/Taste.ATG");
        String tasteATG = new BufferedReader(new InputStreamReader(tasteInputStream))
                .lines()
                .reduce("", (a, b) -> a + "\n" + b)
                .substring(1);

        init(tasteATG);

        // IMPORTS (javacode)
        // COMPILER
        advanceUntil(CocoTypes.KEYWORD_COMPILER);
        assertIdent();

        // GLOBALS (javacode)
        // CHARACTERS
        advanceUntil(CocoTypes.KEYWORD_CHARACTERS);
        assertCharacterDefinition(CocoTypes.STRING);
        assertCharacterDefinition(CocoTypes.STRING);
        assertCharacterDefinition(CocoTypes.CHAR);
        assertCharacterDefinition(CocoTypes.CHAR);
        assertCharacterDefinition(CocoTypes.CHAR);

        // TOKENS
        assertElementType(CocoTypes.KEYWORD_TOKENS);
        assertTokenDefinitionStart();
        assertIdent();
        assertPipe();
        assertIdent();
        assertTokenDefinitionEnd();
        assertTokenDefinitionStart();
        assertIdent();
        assertTokenDefinitionEnd();

        // COMMENTS
        assertCommentDefinition(true);
        assertCommentDefinition(false);

        // IGNORE
        assertElementType(CocoTypes.KEYWORD_IGNORE);
        assertIdent();
        assertElementType(CocoTypes.PLUS);
        assertIdent();
        assertElementType(CocoTypes.PLUS);
        assertIdent();

        // PRODUCTIONS
        assertElementType(CocoTypes.KEYWORD_PRODUCTIONS);
        // Program and declarations
        checkTaste();
        checkVarDecl();
        checkTyp();
        checkProcDecl();
        // Statements
        checkBlock();
        checkStat();
        // Expressions
        checkExpr();

        // END
        assertElementType(CocoTypes.KEYWORD_END);
        assertIdent();
        assertTerminator();
    }

    private void checkExpr() throws IOException {
        //Expr
        assertBlockComment();

        assertIdentWithAttributes();
        assertSemanticAction();

        assertAssignment();
        assertIdentWithAttributes();

        assertBrackOpen();
        assertIdentWithAttributes();

        assertIdentWithAttributes();
        assertSemanticAction();

        assertBrackClose();
        assertTerminator();

        // SimExpr
        assertBlockComment();

        assertIdentWithAttributes();
        assertSemanticAction();

        assertAssignment();
        assertIdentWithAttributes();

        assertCurlOpen();
        assertIdentWithAttributes();

        assertIdentWithAttributes();
        assertSemanticAction();

        assertCurlClose();
        assertTerminator();

        // Term
        assertBlockComment();

        assertIdentWithAttributes();
        assertSemanticAction();

        assertAssignment();
        assertIdentWithAttributes();

        assertCurlOpen();
        assertIdentWithAttributes();

        assertIdentWithAttributes();
        assertSemanticAction();

        assertCurlClose();
        assertTerminator();

        // Factor
        assertBlockComment();

        assertIdentWithAttributes();
        assertSemanticAction();

        assertAssignment();
        assertSemanticAction();

        assertParOpen();
        assertIdentWithAttributes();
        assertSemanticAction();

        assertPipe();
        assertIdent();
        assertSemanticAction();

        assertPipe();
        assertChar();
        assertIdentWithAttributes();
        assertSemanticAction();

        assertPipe();
        assertString();
        assertSemanticAction();

        assertPipe();
        assertString();
        assertSemanticAction();

        assertParClose();
        assertTerminator();

        // Ident
        assertBlockComment();

        assertIdentWithAttributes();

        assertAssignment();
        assertIdent();
        assertSemanticAction();
        assertTerminator();

        // AddOp
        assertBlockComment();

        assertIdentWithAttributes();

        assertAssignment();
        assertSemanticAction();

        assertParOpen();
        assertChar();

        assertPipe();
        assertChar();
        assertSemanticAction();

        assertParClose();
        assertTerminator();

        // MulOp
        assertBlockComment();
        assertIdentWithAttributes();

        assertAssignment();
        assertSemanticAction();

        assertParOpen();
        assertChar();

        assertPipe();
        assertChar();
        assertSemanticAction();

        assertParClose();
        assertTerminator();

        // RelOp
        assertBlockComment();
        assertIdentWithAttributes();

        assertAssignment();
        assertSemanticAction();

        assertParOpen();
        assertString();

        assertPipe();
        assertChar();
        assertSemanticAction();

        assertPipe();
        assertChar();
        assertSemanticAction();

        assertParClose();
        assertTerminator();
    }

    private void checkStat() throws IOException {
        assertBlockComment();

        assertIdentWithAttributes();
        assertSemanticAction();

        assertAssignment();
        assertSemanticAction();

        assertParOpen();
        assertIdentWithAttributes();
        assertSemanticAction();

        assertParOpen();
        assertChar();

        assertIdentWithAttributes();
        assertChar();
        assertSemanticAction();

        assertPipe();
        assertChar();
        assertChar();
        assertChar();
        assertSemanticAction();
        assertParClose();

        // if
        assertPipe();
        assertString();
        assertChar();
        assertIdentWithAttributes();
        assertChar();

        assertIdentWithAttributes();
        assertSemanticAction();

        assertBrackOpen();
        assertString();
        assertIdentWithAttributes();
        assertSemanticAction();

        assertBrackClose();

        // while
        assertPipe();
        assertString();

        assertChar();
        assertIdentWithAttributes();
        assertChar();

        assertIdentWithAttributes();
        assertSemanticAction();

        // read
        assertPipe();
        assertString();

        assertIdentWithAttributes();
        assertChar();
        assertSemanticAction();

        // write
        assertPipe();
        assertString();

        assertIdentWithAttributes();
        assertChar();
        assertSemanticAction();

        // block
        assertPipe();
        assertIdentWithAttributes();
        assertSemanticAction();

        assertParClose();
        assertTerminator();
    }

    private void checkBlock() throws IOException {
        assertBlockComment();

        assertIdentWithAttributes();
        assertSemanticAction();

        assertAssignment();
        assertChar();
        assertSemanticAction();

        assertCurlOpen();
        assertIdentWithAttributes();
        assertSemanticAction();

        assertPipe();
        assertIdent();

        assertCurlClose();
        assertChar();
        assertTerminator();
    }

    private void checkProcDecl() throws IOException {
        assertBlockComment();

        assertIdent();
        assertSemanticAction();

        assertAssignment();
        assertString();

        assertIdentWithAttributes();
        assertSemanticAction();

        assertChar();
        assertChar();

        assertIdentWithAttributes();
        assertSemanticAction();
        assertTerminator();

    }

    private void checkTyp() throws IOException {
        assertBlockComment();

        assertIdentWithAttributes();

        assertAssignment();
        assertSemanticAction();

        assertParOpen();
        assertString();
        assertPipe();
        assertString();
        assertSemanticAction();
        assertParClose();
        assertTerminator();
    }

    private void checkVarDecl() throws IOException {
        assertBlockComment();

        assertIdent();
        assertSemanticAction();

        assertAssignment();
        assertIdentWithAttributes();

        assertIdentWithAttributes();
        assertSemanticAction();

        assertCurlOpen();
        assertChar();
        assertIdentWithAttributes();
        assertSemanticAction();

        assertCurlClose();
        assertChar();
        assertTerminator();
    }

    private void checkTaste() throws IOException {
        assertBlockComment();

        assertIdent();
        assertSemanticAction();

        assertAssignment();
        assertString();

        assertIdentWithAttributes();
        assertSemanticAction();

        assertString();

        assertCurlOpen();
        assertIdent();
        assertPipe();
        assertIdent();
        assertCurlClose();

        assertString();
        assertSemanticAction();
        assertTerminator();
    }

    protected void assertCharacterDefinition(IElementType elementType) throws IOException {
        assertIdent();
        assertAssignment();
        assertElementType(elementType);
        assertElementTypeStrict(CocoTypes.TERMINATOR);
    }

    protected void assertTokenDefinitionStart() throws IOException {
        assertIdent();
        assertAssignment();
        assertIdent();
        assertCurlOpen();
    }

    protected void assertTokenDefinitionEnd() throws IOException {
        assertCurlClose();
        assertElementTypeStrict(CocoTypes.TERMINATOR);
    }

}
