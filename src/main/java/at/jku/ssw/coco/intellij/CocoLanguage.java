package at.jku.ssw.coco.intellij;

import com.intellij.lang.Language;

/**
 * @author Thomas Scheinecker <a href="mailto:tscheinecker@gmail.com">tscheinecker@gmail.com</a>
 */
public class CocoLanguage extends Language {
    public static final CocoLanguage INSTANCE = new CocoLanguage();

    private CocoLanguage() {
        super("Cocol");
    }
}
