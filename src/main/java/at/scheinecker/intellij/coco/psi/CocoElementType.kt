package at.scheinecker.intellij.coco.psi

import at.scheinecker.intellij.coco.CocoLanguage
import com.intellij.psi.tree.IElementType
import org.jetbrains.annotations.NonNls

/**
 * @author Thomas Scheinecker [tscheinecker@gmail.com](mailto:tscheinecker@gmail.com)
 */
class CocoElementType(@NonNls debugName: String) : IElementType(debugName, CocoLanguage.INSTANCE)
