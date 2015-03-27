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
        addInput("import test.*;");
        init("COMPILER");
        advanceUntil(CocoTypes.KEYWORD_COMPILER);
    }

    @Test
    public void testMultipleSimpleImport() throws IOException {
        addInput("import test.abc.Def;");
        addInput("import test.def.ghi.A;");
        init("COMPILER");
        advanceUntil(CocoTypes.KEYWORD_COMPILER);
    }

}
