package at.jku.ssw.coco.intellij;

import at.jku.ssw.coco.intellij.psi.CocoTypes;
import com.intellij.codeInsight.completion.*;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.util.ProcessingContext;

/**
 * Created by Thomas on 27/12/2014.
 */
public class CocoCompletionContributor extends CompletionContributor {
    public CocoCompletionContributor() {
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement(CocoTypes.IDENT).withLanguage(CocoLanguage.INSTANCE),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(CompletionParameters completionParameters, ProcessingContext processingContext, CompletionResultSet completionResultSet) {
//                        completionResultSet.addElement(LookupElementBuilder.);
                    }
                });
    }
}
