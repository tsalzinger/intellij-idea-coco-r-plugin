package me.salzinger.intellij.coco;

import com.intellij.psi.TokenType;
import me.salzinger.intellij.coco.psi.CocoTypes;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by Thomas on 23/03/2015.
 */
public class LexerCompilerTest extends AbstractLexerTest {

    @Test
    public void testSimpleCompiler() throws IOException {
        addInput("COMPILER Taste");
        init("PRODUCTIONS", CocoLexer.STATE_COMPILER);

        assertElementTypeStrict(CocoTypes.KEYWORD_COMPILER);
        assertElementTypeStrict(TokenType.WHITE_SPACE);
        assertElementTypeStrict(CocoTypes.IDENT);
        assertElementTypeStrict(TokenType.WHITE_SPACE);
        assertElementTypeStrict(CocoTypes.EMBEDDED_CODE);
        assertElementTypeStrict(CocoTypes.KEYWORD_PRODUCTIONS);
    }

    @Test
    public void testCompilerWithJavaCodeShort() throws IOException {
        addInput("COMPILER Taste");
        addInput("private String test = \"test\";");
        init("PRODUCTIONS", CocoLexer.STATE_COMPILER);

        assertElementTypeStrict(CocoTypes.KEYWORD_COMPILER);
        assertElementTypeStrict(TokenType.WHITE_SPACE);
        assertElementTypeStrict(CocoTypes.IDENT);
        assertElementTypeStrict(TokenType.WHITE_SPACE);
        assertElementTypeStrict(CocoTypes.EMBEDDED_CODE);
        assertElementTypeStrict(CocoTypes.KEYWORD_PRODUCTIONS);
    }

    @Test
    public void testCompilerWithJavaCodeShort2() throws IOException {
        addInput("COMPILER Taste");
        addInput("private String test = \"test\";");
        addInput("private String test = \"test\";");
        init("PRODUCTIONS", CocoLexer.STATE_COMPILER);

        assertElementTypeStrict(CocoTypes.KEYWORD_COMPILER);
        assertElementTypeStrict(TokenType.WHITE_SPACE);
        assertElementTypeStrict(CocoTypes.IDENT);
        assertElementTypeStrict(TokenType.WHITE_SPACE);
        assertElementTypeStrict(CocoTypes.EMBEDDED_CODE);
        assertElementTypeStrict(CocoTypes.KEYWORD_PRODUCTIONS);
    }

    @Test
    public void testCompilerWithJavaCode() throws IOException {
        addInput("COMPILER Taste");
        addInput("private String test = \"test\";");
        addInput("// line comment within java code");
        addInput("// another one with ( parenthesis )");
        addInput("/* and a block comment */");
        addInput("and some illegal code");
        addInput("private long abc = -1;");
        init("PRODUCTIONS");

        assertElementTypeStrict(CocoTypes.KEYWORD_COMPILER);
        assertElementTypeStrict(TokenType.WHITE_SPACE);
        assertElementTypeStrict(CocoTypes.IDENT);
        assertElementTypeStrict(TokenType.WHITE_SPACE);
        assertElementTypeStrict(CocoTypes.EMBEDDED_CODE);
        assertElementTypeStrict(CocoTypes.KEYWORD_PRODUCTIONS);
    }

    @Test
    public void testCompilerWithJavaCodeAndIgnorecase() throws IOException {
        addInput("COMPILER Taste");
        addInput("private String test = \"test\";");
        addInput("// line comment within java code");
        addInput("// another one with ( parenthesis )");
        addInput("/* and a block comment */");
        addInput("and some illegal code");
        addInput("private long abc = -1;");
        init("IGNORECASE", CocoLexer.STATE_COMPILER);

        assertElementTypeStrict(CocoTypes.KEYWORD_COMPILER);
        assertElementTypeStrict(TokenType.WHITE_SPACE);
        assertElementTypeStrict(CocoTypes.IDENT);
        assertElementTypeStrict(TokenType.WHITE_SPACE);
        assertElementTypeStrict(CocoTypes.EMBEDDED_CODE);
        assertElementTypeStrict(CocoTypes.KEYWORD_IGNORECASE);

    }
}
