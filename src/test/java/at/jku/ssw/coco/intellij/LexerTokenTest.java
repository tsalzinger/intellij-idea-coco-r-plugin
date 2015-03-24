package at.jku.ssw.coco.intellij;

import at.jku.ssw.coco.intellij.psi.CocoTypes;
import com.intellij.psi.tree.IElementType;
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
public class LexerTokenTest extends AbstractLexerTest {
    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"COMPILER", CocoTypes.TOKEN_COMPILER},
                {"CHARACTERS", CocoTypes.TOKEN_CHARACTERS},
                {"TOKENS", CocoTypes.TOKEN_TOKENS},
                {"CONTEXT", CocoTypes.TOKEN_CONTEXT},
                {"IGNORECASE", CocoTypes.TOKEN_IGNORECASE},
                {"PRAGMAS", CocoTypes.TOKEN_PRAGMAS},
                {"PRODUCTIONS", CocoTypes.TOKEN_PRODUCTIONS},
                {"END", CocoTypes.TOKEN_END}
        });
    }

    private final String token;
    private final IElementType elementType;

    public LexerTokenTest(String token, IElementType elementType) {
        this.token = token;
        this.elementType = elementType;
    }

    @Test
    public void testToken() throws IOException {
        init(token);
        assertElementTypeStrict(elementType);
    }
}
