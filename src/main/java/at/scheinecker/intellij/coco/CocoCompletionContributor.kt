package at.scheinecker.intellij.coco

import at.scheinecker.intellij.coco.psi.CocoTypes
import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.patterns.PlatformPatterns
import com.intellij.util.ProcessingContext
import java.util.*

/**
 * Created by Thomas on 27/12/2014.
 */
class CocoCompletionContributor : CompletionContributor() {
    init {
        extend(CompletionType.BASIC,
                PlatformPatterns
                        .psiElement(CocoTypes.IDENT)
                        .withParent(PlatformPatterns.psiElement(CocoTypes.END))
                        .withLanguage(CocoLanguage.INSTANCE),
                object : CompletionProvider<CompletionParameters>() {
                    override fun addCompletions(completionParameters: CompletionParameters, processingContext: ProcessingContext?, completionResultSet: CompletionResultSet) {
                        val compilers = CocoUtil.findCompilers(completionParameters.originalFile)

                        for (compiler in compilers) {
                            if (Objects.isNull(compiler.name)) {
                                continue
                            }

                            val compilerNameLookupElement = LookupElementBuilder
                                    .create(compiler.name!!)
                                    .withIcon(CocoIcons.FILE)
                                    .withTypeText(compiler.containingFile.name)
                            completionResultSet.addElement(compilerNameLookupElement)
                        }

                    }
                })

//        extend(CompletionType.BASIC,
//                PlatformPatterns
//                        .or(
//                                PlatformPatterns.psiElement(CocoTypes.IDENT).withParent(CocoTokenDecl::class.java),
//                                PlatformPatterns.psiElement(CocoTypes.IDENT).withParent(CocoSetDecl::class.java)
//                        ),
//                object : CompletionProvider<CompletionParameters>() {
//                    override fun addCompletions(completionParameters: CompletionParameters, processingContext: ProcessingContext?, completionResultSet: CompletionResultSet) {
//                        completionResultSet.addElement(LookupElementBuilder.create("").withIcon(CocoIcons.FILE))
//                    }
//                }
//
//        )
//
//        extend(CompletionType.BASIC,
//                PlatformPatterns.psiElement(CocoTypes.ASSIGNMENT),
//
//                object : CompletionProvider<CompletionParameters>() {
//                    override fun addCompletions(completionParameters: CompletionParameters, processingContext: ProcessingContext?, completionResultSet: CompletionResultSet) {
//                        completionResultSet.addElement(LookupElementBuilder.create("").withIcon(CocoIcons.FILE))
//                    }
//                })
    }
}
