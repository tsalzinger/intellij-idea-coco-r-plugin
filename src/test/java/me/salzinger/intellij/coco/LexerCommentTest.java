package me.salzinger.intellij.coco;

import me.salzinger.intellij.coco.psi.CocoTypes;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by Thomas on 23/03/2015.
 */
public class LexerCommentTest extends AbstractLexerTest {

    @Test
    public void testSimpleLineComment() throws IOException {
        init("//TEST");
        assertElementTypeStrict(CocoTypes.LINE_COMMENT);
    }
    @Test
    public void testLineCommentWithParenthesis() throws IOException {
        init("//TEST(some note) (some other note)");
        assertElementTypeStrict(CocoTypes.LINE_COMMENT);
    }

    @Test
    public void testSimpleBlockComment() throws IOException {
        init("/* TEST */");
        assertElementTypeStrict(CocoTypes.BLOCK_COMMENT);
    }

    @Test
    public void testMultilineBlockComment() throws IOException {
        init("/* FIRST \n SECOND */");
        assertElementTypeStrict(CocoTypes.BLOCK_COMMENT);
    }

    @Test
    public void testDocBlockComment() throws IOException {
        init("/************\n * SECOND \n ******************/");
        assertElementTypeStrict(CocoTypes.BLOCK_COMMENT);
    }

    @Test
    public void testDocBlockComment2() throws IOException {
        init("/***************/");
        assertElementTypeStrict(CocoTypes.BLOCK_COMMENT);
    }
}
