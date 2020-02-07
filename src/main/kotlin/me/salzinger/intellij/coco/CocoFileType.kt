package me.salzinger.intellij.coco

import com.intellij.openapi.fileTypes.LanguageFileType
import javax.swing.Icon

/**
 * @author Thomas Salzinger [tsalzinger@gmail.com](mailto:tsalzinger@gmail.com)
 */
class CocoFileType private constructor() : LanguageFileType(CocoLanguage.INSTANCE) {

    override fun getName(): String {
        return "Cocol/R file"
    }

    override fun getDescription(): String {
        return "Coco/R ATG"
    }

    override fun getDefaultExtension(): String {
        return "ATG"
    }

    override fun getIcon(): Icon? {
        return CocoIcons.FILE
    }

    companion object {
        val INSTANCE = CocoFileType()
    }
}
