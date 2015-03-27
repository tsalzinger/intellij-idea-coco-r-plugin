package at.jku.ssw.coco.intellij;

import at.jku.ssw.coco.intellij.psi.CocoTypes;
import com.intellij.lexer.FlexLexer;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Thomas on 23/03/2015.
 */
public class AbstractLexerTest {
    protected FlexLexer cocoLexer = new CocoLexerAdapter().getFlex();

    private StringBuilder input;

    @Before
    public void initInput() {
        input = new StringBuilder();
    }

    protected void init(String content) {
        addInput(content);
        init();

        CocoIcons.FILE.toString();
    }

    protected void addInput(String append) {
        if (input.length() > 0) {
            input.append("\n");
        }
        input.append(append);
    }

    protected void init() {
        cocoLexer.reset(input.toString(), 0, input.length(), CocoLexer.YYINITIAL);
    }

    protected IElementType advanceUntil(IElementType elementType) throws IOException {
        IElementType advance = cocoLexer.advance();

        while (advance != null && !elementType.equals(advance)) {
            advance = cocoLexer.advance();
        }


        Assert.assertEquals(elementType, advance);

        return advance;
    }

    protected void assertElementType(IElementType... elementTypes) throws IOException {
        try {
            IElementType advance = cocoLexer.advance();


            while (TokenType.WHITE_SPACE.equals(advance)) {
                advance = cocoLexer.advance();
            }

            char[] currentInput = new char[cocoLexer.getTokenEnd()-cocoLexer.getTokenStart()];
            input.getChars(cocoLexer.getTokenStart(), cocoLexer.getTokenEnd(), currentInput, 0);

            if (elementTypes.length == 1) {
                Assert.assertEquals(elementTypes[0], advance);
                System.out.print(elementTypes[0]);
                System.out.print(": ");
                System.out.println(currentInput);
                return;
            }

            List<IElementType> elementTypesList = Arrays.asList(elementTypes);
            StringBuilder sb = new StringBuilder();
            sb.append("[");

            for (IElementType elementType : elementTypesList) {
                sb.append(elementType);
                sb.append(", ");
            }

            sb.setLength(sb.length() - 2);

            sb.append("]");
            if (!elementTypesList.contains(advance)) {

                Assert.fail("expected " + sb.toString() + " to contain " + advance);
            }


            System.out.print(sb.toString());
            System.out.print(": ");
            System.out.println(currentInput);
        } catch (Error e) {
            if ("Error: could not match input".equals(e.getMessage())) {
                throw new Error("Error: Couldn't match " + input.toString());
            }
            throw e;
        }
    }

    protected void assertElementTypeStrict(IElementType elementType) throws IOException {
        try {
            IElementType advance = cocoLexer.advance();
            Assert.assertEquals(elementType, advance);
        } catch (Error e) {
            if ("Error: could not match input".equals(e.getMessage())) {
                throw new Error("Error: Couldn't match " + input.toString());
            }
            throw e;
        }
    }

    @After
    public void checkInputHandled() throws IOException {
        Assert.assertNull(cocoLexer.advance());
    }

    protected void assertBrackClose() throws IOException {
        assertElementType(CocoTypes.BRACK_CLOSE);
    }

    protected void assertBrackOpen() throws IOException {
        assertElementType(CocoTypes.BRACK_OPEN);
    }

    protected void assertSemanticAction() throws IOException {
        assertElementType(CocoTypes.SEM_ACTION_START);
        advanceUntil(CocoTypes.SEM_ACTION_END);
    }

    protected void assertBlockComment() throws IOException {
        assertElementType(CocoTypes.BLOCK_COMMENT);
    }

    protected void assertParClose() throws IOException {
        assertElementType(CocoTypes.PAR_CLOSE);
    }

    protected void assertParOpen() throws IOException {
        assertElementType(CocoTypes.PAR_OPEN);
    }

    protected void assertString() throws IOException {
        assertElementType(CocoTypes.STRING);
    }

    protected void assertChar() throws IOException {
        assertElementType(CocoTypes.CHAR);
    }

    protected void assertCurlOpen() throws IOException {
        assertElementType(CocoTypes.CURL_OPEN);
    }

    protected void assertIdentWithAttributes() throws IOException {
        assertIdent();
        assertElementType(CocoTypes.SMALLER_THEN);
        advanceUntil(CocoTypes.GREATER_THEN);
    }

    protected void assertIdent() throws IOException {
        assertElementType(CocoTypes.IDENT);
    }

    protected void assertPipe() throws IOException {
        assertElementType(CocoTypes.PIPE);
    }

    protected void assertCurlClose() throws IOException {
        assertElementType(CocoTypes.CURL_CLOSE);
    }

    protected void assertTerminator() throws IOException {
        assertElementType(CocoTypes.TERMINATOR);
    }

    protected void assertAssignment() throws IOException {
        assertElementType(CocoTypes.ASSIGNMENT);
    }

    protected void assertCommentDefinition(boolean nested) throws IOException {
        assertElementType(CocoTypes.KEYWORD_COMMENTS);
        assertElementType(CocoTypes.KEYWORD_FROM);
        assertElementType(CocoTypes.STRING, CocoTypes.IDENT);
        assertElementType(CocoTypes.KEYWORD_TO);
        assertElementType(CocoTypes.STRING, CocoTypes.IDENT);
        if (nested) {
            assertElementType(CocoTypes.KEYWORD_NESTED);
        }
    }
}
