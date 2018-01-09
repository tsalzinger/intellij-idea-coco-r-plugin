package at.scheinecker.intellij.coco

import com.intellij.lang.Language

/**
 * @author Thomas Scheinecker [tscheinecker@gmail.com](mailto:tscheinecker@gmail.com)
 */
class CocoLanguage private constructor() : Language("Cocol/R") {
    companion object {
        val INSTANCE = CocoLanguage()
    }
}
