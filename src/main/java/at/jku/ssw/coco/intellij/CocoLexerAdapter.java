package at.jku.ssw.coco.intellij;

import com.intellij.lexer.FlexAdapter;

import java.io.Reader;

/**
 * @author Thomas Scheinecker <a href="mailto:tscheinecker@gmail.com">tscheinecker@gmail.com</a>
 */
public class CocoLexerAdapter extends FlexAdapter {
    public CocoLexerAdapter() {
        super(new CocoLexer((Reader) null));
    }
}
