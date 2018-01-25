package at.scheinecker.intellij.coco.psi

import at.scheinecker.intellij.coco.CocoFileType
import at.scheinecker.intellij.coco.CocoLanguage
import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider

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
