package at.scheinecker.intellij.coco.java

import at.scheinecker.intellij.coco.CocoUtil
import at.scheinecker.intellij.coco.psi.CocoFile
import com.intellij.codeInsight.daemon.impl.HighlightInfo
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.ExternalAnnotator
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile

class CocoJavaExternalAnnotator : ExternalAnnotator<CocoFile, List<HighlightInfo>>() {
    override fun collectInformation(file: PsiFile, editor: Editor, hasErrors: Boolean): CocoFile? {
        return file as? CocoFile
    }

    override fun doAnnotate(collectedInfo: CocoFile): List<HighlightInfo> {
        return CocoJavaUtil.getJavaErrors(collectedInfo)
    }

    override fun apply(file: PsiFile, annotationResult: List<HighlightInfo>, holder: AnnotationHolder) {
        val parserClass = CocoJavaUtil.getParserClass(file) ?: return

        val virtualFile = parserClass.containingFile.virtualFile
        val document = FileDocumentManager.getInstance().getDocument(virtualFile) ?: return

        val globalFieldsAndMethods = CocoUtil.findGlobalFieldsAndMethods(file)

        if (globalFieldsAndMethods != null) {
            val offset = document.text.indexOf(globalFieldsAndMethods.text)
            if (offset != -1) {
                for (highlightInfo in findInRange(annotationResult, TextRange.from(offset, globalFieldsAndMethods.textLength))) {
                    holder.createErrorAnnotation(TextRange.from(
                            globalFieldsAndMethods.textOffset + highlightInfo.startOffset - offset,
                            highlightInfo.endOffset - highlightInfo.startOffset
                    ), highlightInfo.description)
                }

            }
        }

        val pragmas = CocoUtil.findScannerSpecification(file)?.pragmas
        if (pragmas != null) {
            val pragmaMethod = parserClass.findMethodsByName("Get", false).firstOrNull()
            if (pragmaMethod != null) {
                for (highlightInfo in findInRange(annotationResult, pragmaMethod.textRange)) {
                    val offset = findOffset(highlightInfo, document, pragmas)

                    holder.createErrorAnnotation(
                            offset ?: pragmas.firstChild.textRange,
                            highlightInfo.description
                    )
                }
            }
        }

        val productions = CocoUtil.findProductions(file)
        productions.forEach {
            val productionDeclaration = parserClass.findMethodsByName(it.name, false).firstOrNull()

            if (productionDeclaration != null) {
                for (highlightInfo in findInRange(annotationResult, productionDeclaration.textRange)) {
                    val offset = findOffset(highlightInfo, document, it)

                    holder.createErrorAnnotation(
                            offset ?: it.ident.textRange,
                            highlightInfo.description
                    )
                }
            }
        }
    }

    fun findInRange(highlightInfos: List<HighlightInfo>, textRange: TextRange): List<HighlightInfo> {
        return highlightInfos
                .filter { textRange.contains(it) }
    }

    private fun findOffset(
            info: HighlightInfo,
            document: Document,
            containingElement: PsiElement
    ): TextRange? {
        val text = document.getText(TextRange.create(info)).trim()
        val searchParts = containingElement.text.split(text)
        val count = searchParts.count()

        return if (count == 2) {
            TextRange.from(containingElement.textOffset + searchParts[0].length, text.length)
        }
        else null
    }
}