package me.salzinger.intellij.coco.psi

import com.intellij.psi.tree.IElementType
import me.salzinger.intellij.coco.CocoLanguage
import org.jetbrains.annotations.NonNls

/**
 * @author Thomas Scheinecker [tscheinecker@gmail.com](mailto:tscheinecker@gmail.com)
 */
class CocoElementType(@NonNls debugName: String) : IElementType(debugName, CocoLanguage.INSTANCE)
