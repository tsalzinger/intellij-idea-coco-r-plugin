package at.scheinecker.intellij.coco.psi

import at.scheinecker.intellij.coco.CocoLanguage
import com.intellij.psi.tree.IElementType
import org.jetbrains.annotations.NonNls

/**
 * @author Thomas Scheinecker [tscheinecker@gmail.com](mailto:tscheinecker@gmail.com)
 */
class CocoTokenType(@NonNls debugName: String) : IElementType(debugName, CocoLanguage.INSTANCE) {

    override fun toString(): String {
        return "CocoTokenType." + super.toString()
    }
}
