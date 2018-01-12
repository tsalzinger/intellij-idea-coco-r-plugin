package at.scheinecker.intellij.coco

import com.intellij.lang.Language

/**
 * @author Thomas Scheinecker [tscheinecker@gmail.com](mailto:tscheinecker@gmail.com)
 */
class CocoLanguage private constructor() : Language(ID) {
    companion object {
        const val ID = "Cocol/R"
        val INSTANCE = CocoLanguage()
    }
}
