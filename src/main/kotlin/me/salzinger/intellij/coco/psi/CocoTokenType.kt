package me.salzinger.intellij.coco.psi

import com.intellij.psi.tree.IElementType
import me.salzinger.intellij.coco.CocoLanguage
import org.jetbrains.annotations.NonNls

/**
 * @author Thomas Salzinger [tsalzinger@gmail.com](mailto:tsalzinger@gmail.com)
 */
class CocoTokenType(@NonNls debugName: String) : IElementType(debugName, CocoLanguage.INSTANCE)
