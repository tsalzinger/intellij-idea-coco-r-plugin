package at.scheinecker.intellij.coco

import com.intellij.openapi.fileTypes.FileTypeConsumer
import com.intellij.openapi.fileTypes.FileTypeFactory

/**
 * @author Thomas Scheinecker [tscheinecker@gmail.com](mailto:tscheinecker@gmail.com)
 */
class CocoFileTypeFactory : FileTypeFactory() {
    override fun createFileTypes(fileTypeConsumer: FileTypeConsumer) {
        fileTypeConsumer.consume(CocoFileType.INSTANCE, CocoFileType.INSTANCE.defaultExtension)
    }
}
