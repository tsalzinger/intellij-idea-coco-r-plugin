package at.jku.ssw.coco.intellij;

import at.jku.ssw.coco.intellij.psi.CocoTypes;
import com.intellij.psi.TokenType;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by Thomas on 23/03/2015.
 */
public class LexerImportTest extends AbstractLexerTest {

    @Test
    public void testSimpleImport() throws IOException {
        init("import test.*;");
        assertImport();
    }

    @Test
    public void testMultipleSimpleImport() throws IOException {
        addInput("import test.abc.Def;");
        addInput("import test.def.ghi.A;");
        init();
        assertImports(2);
    }

    private void assertImport() throws IOException {
        assertImports(1);
    }

    private void assertImports(int count) throws IOException {
        for (int i = 1; i <= count; i++) {
            assertElementTypeStrict(CocoTypes.IMPORT);
            assertElementTypeStrict(TokenType.WHITE_SPACE);
            assertElementTypeStrict(CocoTypes.IMPORTPATH);
            if (i != count) {
                assertElementTypeStrict(TokenType.WHITE_SPACE);
            }
        }
    }

}
