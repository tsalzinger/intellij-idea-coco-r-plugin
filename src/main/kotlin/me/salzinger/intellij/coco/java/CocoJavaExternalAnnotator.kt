package me.salzinger.intellij.coco.java

import com.intellij.codeInsight.daemon.impl.HighlightInfo
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.ExternalAnnotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import me.salzinger.intellij.coco.findGlobalFieldsAndMethods
import me.salzinger.intellij.coco.findProductions
import me.salzinger.intellij.coco.findScannerSpecification
import me.salzinger.intellij.coco.psi.CocoFile
import me.salzinger.intellij.coco.psi.CocoGlobalFieldsAndMethods
import me.salzinger.intellij.coco.psi.CocoPragmas
import me.salzinger.intellij.coco.psi.CocoProduction

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

        findGlobalFieldsAndMethods(file)
            ?.annotateGlobalFieldsAndMethods(document, annotationResult, holder)

        findScannerSpecification(file)
            ?.pragmas
            ?.annotatePragmas(
                parserClass = parserClass,
                annotationResult = annotationResult,
                document = document,
                holder = holder
            )

        findProductions(file)
            .annotateProductions(
                parserClass = parserClass,
                annotationResult = annotationResult,
                document = document,
                holder = holder
            )
    }

    private fun List<CocoProduction>.annotateProductions(
        parserClass: PsiClass,
        annotationResult: List<HighlightInfo>,
        document: Document,
        holder: AnnotationHolder,
    ) {
        forEach { production ->
            val productionDeclaration = parserClass.findMethodsByName(production.name, false).firstOrNull()

            if (productionDeclaration != null) {
                for (highlightInfo in annotationResult.filterInRange(productionDeclaration.textRange)) {
                    val offset = highlightInfo.findOffset(document, production)

                    holder
                        .newAnnotation(
                            HighlightSeverity.ERROR,
                            highlightInfo.description
                        )
                        .range(offset ?: production.ident.textRange)
                        .create()
                }
            }
        }
    }

    private fun CocoPragmas.annotatePragmas(
        parserClass: PsiClass,
        annotationResult: List<HighlightInfo>,
        document: Document,
        holder: AnnotationHolder,
    ) {
        val pragmaMethod = parserClass.findMethodsByName("Get", false).firstOrNull()
        if (pragmaMethod != null) {
            for (highlightInfo in annotationResult.filterInRange(pragmaMethod.textRange)) {
                val offset = highlightInfo.findOffset(document, this)

                holder
                    .newAnnotation(
                        HighlightSeverity.ERROR,
                        highlightInfo.description
                    )
                    .range(offset ?: firstChild.textRange)
                    .create()
            }
        }
    }

    private fun CocoGlobalFieldsAndMethods.annotateGlobalFieldsAndMethods(
        document: Document,
        annotationResult: List<HighlightInfo>,
        holder: AnnotationHolder,
    ) {
        val offset = document.text.indexOf(text)
        if (offset != -1) {
            val highlightInfos = annotationResult.filterInRange(
                TextRange.from(offset, textLength)
            )
            for (highlightInfo in highlightInfos) {
                holder
                    .newAnnotation(
                        HighlightSeverity.ERROR,
                        highlightInfo.description
                    )
                    .range(
                        TextRange.from(
                            textOffset + highlightInfo.startOffset - offset,
                            highlightInfo.endOffset - highlightInfo.startOffset
                        )
                    )
                    .create()
            }
        }
    }

    private fun List<HighlightInfo>.filterInRange(textRange: TextRange): List<HighlightInfo> {
        return filter { textRange.contains(it) }
    }

    private fun HighlightInfo.findOffset(
        document: Document,
        containingElement: PsiElement,
    ): TextRange? {
        val text = document.getText(TextRange.create(this)).trim()
        val searchParts = containingElement.text.split(text)
        val count = searchParts.count()

        return if (count == 2) {
            TextRange.from(containingElement.textOffset + searchParts[0].length, text.length)
        } else null
    }
}
