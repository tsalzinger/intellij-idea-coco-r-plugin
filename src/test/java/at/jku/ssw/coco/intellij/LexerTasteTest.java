package at.jku.ssw.coco.intellij;

import at.jku.ssw.coco.intellij.psi.CocoTypes;
import com.intellij.psi.tree.IElementType;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.chrono.AbstractChronology;

/**
 * Created by Thomas on 23/03/2015.
 */
public class LexerTasteTest extends AbstractLexerTest {


    @Test
    public void testTasteATG() throws IOException {
        final InputStream tasteInputStream = LexerTasteTest.class.getResourceAsStream("/at/jku/ssw/coco/intellij/Taste.ATG");
        String tasteATG = new BufferedReader(new InputStreamReader(tasteInputStream))
                .lines()
                .reduce("", (a, b) -> a + "\n" + b)
                .substring(1);

        init(tasteATG);

        // COMPILER
        assertElementTypeStrict(CocoTypes.TOKEN_COMPILER);
        assertIdent();

        // GLOBALS (javacode)
        assertElementType(CocoTypes.JAVACODE);

        // CHARACTERS
        assertElementTypeStrict(CocoTypes.TOKEN_CHARACTERS);
        assertCharacterDefinition(CocoTypes.STRING);
        assertCharacterDefinition(CocoTypes.STRING);
        assertCharacterDefinition(CocoTypes.CHAR);
        assertCharacterDefinition(CocoTypes.CHAR);
        assertCharacterDefinition(CocoTypes.CHAR);

        // TOKENS
        assertElementType(CocoTypes.TOKEN_TOKENS);
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
        assertElementType(CocoTypes.IGNORE);
        assertIdent();
        assertElementType(CocoTypes.PLUS);
        assertIdent();
        assertElementType(CocoTypes.PLUS);
        assertIdent();

        // PRODUCTIONS
        assertElementType(CocoTypes.TOKEN_PRODUCTIONS);
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
        assertElementType(CocoTypes.TOKEN_END);
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

    private void assertBrackClose() throws IOException {
        assertElementType(CocoTypes.BRACK_CLOSE);
    }

    private void assertBrackOpen() throws IOException {
        assertElementType(CocoTypes.BRACK_OPEN);
    }

    private void assertSemanticAction() throws IOException {
        assertElementType(CocoTypes.SEM_ACTION_);
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

    private void assertBlockComment() throws IOException {
        assertElementType(CocoTypes.BLOCK_COMMENT);
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

    private void assertParClose() throws IOException {
        assertElementType(CocoTypes.PAR_CLOSE);
    }

    private void assertParOpen() throws IOException {
        assertElementType(CocoTypes.PAR_OPEN);
    }

    private void assertString() throws IOException {
        assertElementType(CocoTypes.STRING);
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

    private void assertChar() throws IOException {
        assertElementType(CocoTypes.CHAR);
    }

    private void assertCurlOpen() throws IOException {
        assertElementType(CocoTypes.CURL_OPEN);
    }

    private void assertIdentWithAttributes() throws IOException {
        assertIdent();
        assertElementType(CocoTypes.SMALLER_THEN);
        assertElementType(CocoTypes.ARBITRARY_TEXT);
        assertElementType(CocoTypes.GREATER_THEN);
    }

    private void assertIdent() throws IOException {
        assertElementType(CocoTypes.IDENT);
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

    private void assertPipe() throws IOException {
        assertElementType(CocoTypes.PIPE);
    }

    private void assertCurlClose() throws IOException {
        assertElementType(CocoTypes.CURL_CLOSE);
    }

    private void assertTerminator() throws IOException {
        assertElementType(CocoTypes.TERMINATOR);
    }

    private void assertAssignment() throws IOException {
        assertElementType(CocoTypes.ASSIGNMENT);
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

    protected void assertCommentDefinition(boolean nested) throws IOException {
        assertElementType(CocoTypes.COMMENTS);
        assertElementType(CocoTypes.FROM);
        assertElementType(CocoTypes.STRING, CocoTypes.IDENT);
        assertElementType(CocoTypes.TO);
        assertElementType(CocoTypes.STRING, CocoTypes.IDENT);
        if (nested) {
            assertElementType(CocoTypes.NESTED);
        }
    }
}
