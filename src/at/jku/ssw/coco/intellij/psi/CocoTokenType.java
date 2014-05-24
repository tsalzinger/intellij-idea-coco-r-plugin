package at.jku.ssw.coco.intellij.psi;

import at.jku.ssw.coco.intellij.CocoLanguage;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Scheinecker <a href="mailto:tscheinecker@gmail.com">tscheinecker@gmail.com</a>
 */
public class CocoTokenType extends IElementType {
    public CocoTokenType(@NotNull @NonNls String debugName) {
        super(debugName, CocoLanguage.INSTANCE);
    }

    @Override
    public String toString() {
        return "CocoTokenType." + super.toString();
    }
}
