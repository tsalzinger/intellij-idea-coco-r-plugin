package at.jku.ssw.coco.intellij;

import at.jku.ssw.coco.intellij.psi.CocoTypes;
import com.intellij.psi.TokenType;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by Thomas on 23/03/2015.
 */
public class LexerCompilerTest extends AbstractLexerTest {

    @Test
    public void testSimpleCompiler() throws IOException {
        init("COMPILER Taste");

        assertElementTypeStrict(CocoTypes.TOKEN_COMPILER);
        assertElementTypeStrict(TokenType.WHITE_SPACE);
        assertElementTypeStrict(CocoTypes.IDENT);
        assertElementTypeStrict(CocoTypes.JAVACODE);
    }

    @Test
    public void testCompilerWithJavaCodeShort() throws IOException {
        addInput("COMPILER Taste");
        init("private String test = \"test\";");

        assertElementTypeStrict(CocoTypes.TOKEN_COMPILER);
        assertElementTypeStrict(TokenType.WHITE_SPACE);
        assertElementTypeStrict(CocoTypes.IDENT);
        assertElementTypeStrict(CocoTypes.JAVACODE);
    }

    @Test
    public void testCompilerWithJavaCodeShort2() throws IOException {
        addInput("COMPILER Taste");
        init("private String test = \"test\";");
        init("private String test = \"test\";");

        assertElementTypeStrict(CocoTypes.TOKEN_COMPILER);
        assertElementTypeStrict(TokenType.WHITE_SPACE);
        assertElementTypeStrict(CocoTypes.IDENT);
        assertElementTypeStrict(CocoTypes.JAVACODE);
    }

    @Test
    public void testCompilerWithJavaCode() throws IOException {
        addInput("COMPILER Taste");
        addInput("private String test = \"test\";");
        addInput("// line comment within java code");
        addInput("// another one with ( parenthesis )");
        addInput("/* and a block comment */");
        addInput("and some illegal code");
        init("private long abc = -1;");

        assertElementTypeStrict(CocoTypes.TOKEN_COMPILER);
        assertElementTypeStrict(TokenType.WHITE_SPACE);
        assertElementTypeStrict(CocoTypes.IDENT);
        assertElementTypeStrict(CocoTypes.JAVACODE);
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
        addInput("IGNORECASE");
        init();

        assertElementTypeStrict(CocoTypes.TOKEN_COMPILER);
        assertElementTypeStrict(TokenType.WHITE_SPACE);
        assertElementTypeStrict(CocoTypes.IDENT);
        assertElementTypeStrict(CocoTypes.JAVACODE);
        assertElementTypeStrict(CocoTypes.TOKEN_IGNORECASE);

    }
}
