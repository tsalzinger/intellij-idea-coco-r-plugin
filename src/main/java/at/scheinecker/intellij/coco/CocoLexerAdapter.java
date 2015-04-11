package at.scheinecker.intellij.coco;

import com.intellij.lexer.FlexAdapter;

/**
 * @author Thomas Scheinecker <a href="mailto:tscheinecker@gmail.com">tscheinecker@gmail.com</a>
 */
public class CocoLexerAdapter extends FlexAdapter {
    public CocoLexerAdapter() {
        super(new CocoLexer());
    }
}
