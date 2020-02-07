package me.salzinger.intellij.coco

import com.intellij.lang.Language

/**
 * @author Thomas Salzinger [tsalzinger@gmail.com](mailto:tsalzinger@gmail.com)
 */
class CocoLanguage private constructor() : Language(ID) {
    companion object {
        const val ID = "Cocol/R"
        val INSTANCE = CocoLanguage()
    }
}
