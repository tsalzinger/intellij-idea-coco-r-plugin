package at.scheinecker.intellij.coco;

import at.scheinecker.intellij.coco.psi.CocoTypes;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by Thomas on 23/03/2015.
 */
@RunWith(Parameterized.class)
public class LexerCharTest extends AbstractLexerTest {
    private final String string;

    @Parameterized.Parameters
    public static Collection<String[]> data() {
        String[][] strings = {
                {"'a'"},
                {"'B'"},
                {"' '"},
                {"'*'"},
                {"'\\\''"},
                {"'\"'"},
                {"'\\\"'"},
                {"'\\n'"},
                {"'\\r'"},
                {"'\\0'"},
                {"'\\a'"},
                {"'\\b'"},
                {"'\\t'"},
                {"'\\v'"},
                {"'\\f'"},
                {"'\\\\'"},
                {"'\\u0DF3'"}
        };
        return Arrays.asList(strings);
    }

    public LexerCharTest(String string) {
        this.string = string;
    }

    @Test
    public void testString() throws IOException {
        init(string, CocoLexer.STATE_COMPILER);
        assertElementTypeStrict(CocoTypes.CHAR);
    }
}
