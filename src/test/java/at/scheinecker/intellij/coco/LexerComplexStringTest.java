package at.scheinecker.intellij.coco;

import at.scheinecker.intellij.coco.psi.CocoTypes;
import com.intellij.psi.TokenType;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by Thomas on 23/03/2015.
 */
public class LexerComplexStringTest extends AbstractLexerTest {

    @Test
    public void testEscapeStringLazyString() throws IOException {
        init("\"\\\\\" \"second\"");
        assertElementTypeStrict(CocoTypes.STRING);
        assertElementTypeStrict(TokenType.WHITE_SPACE);
        assertElementTypeStrict(CocoTypes.STRING);
    }
}
