package me.salzinger.intellij.coco.psi

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider
import me.salzinger.intellij.coco.CocoFileType
import me.salzinger.intellij.coco.CocoLanguage

/**
 * @author Thomas Scheinecker [tscheinecker@gmail.com](mailto:tscheinecker@gmail.com)
 */
class CocoFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, CocoLanguage.INSTANCE) {

    override fun getFileType(): FileType {
        return CocoFileType.INSTANCE
    }

    override fun toString(): String {
        return "Coco File"
    }
}
