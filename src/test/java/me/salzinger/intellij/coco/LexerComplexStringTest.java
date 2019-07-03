package me.salzinger.intellij.coco;

import com.intellij.psi.TokenType;
import me.salzinger.intellij.coco.psi.CocoTypes;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by Thomas on 23/03/2015.
 */
public class LexerComplexStringTest extends AbstractLexerTest {

    @Test
    public void testEscapeStringLazyString() throws IOException {
        init("\"\\\\\" \"second\"", CocoLexer.STATE_COMPILER);
        assertElementTypeStrict(CocoTypes.STRING);
        assertElementTypeStrict(TokenType.WHITE_SPACE);
        assertElementTypeStrict(CocoTypes.STRING);
    }
}
