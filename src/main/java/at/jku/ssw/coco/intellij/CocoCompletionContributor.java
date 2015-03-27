package at.jku.ssw.coco.intellij;

import at.jku.ssw.coco.intellij.psi.CocoCompiler;
import at.jku.ssw.coco.intellij.psi.CocoTypes;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

/**
 * Created by Thomas on 27/12/2014.
 */
public class CocoCompletionContributor extends CompletionContributor {
    public CocoCompletionContributor() {
        extend(CompletionType.BASIC,
                PlatformPatterns
                        .psiElement(CocoTypes.IDENT)
                        .withParent(PlatformPatterns.psiElement(CocoTypes.END))
                        .withLanguage(CocoLanguage.INSTANCE),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters completionParameters, ProcessingContext processingContext, @NotNull CompletionResultSet completionResultSet) {
                        List<CocoCompiler> compilers = CocoUtil.findCompilers(completionParameters.getOriginalFile());

                        for (CocoCompiler compiler : compilers) {
                            if (Objects.isNull(compiler.getName())) {
                                continue;
                            }

                            LookupElementBuilder compilerNameLookupElement = LookupElementBuilder
                                    .create(compiler.getName())
                                    .withIcon(CocoIcons.FILE)
                                    .withTypeText(compiler.getContainingFile().getName());
                            completionResultSet.addElement(compilerNameLookupElement);
                        }

                    }
                });
    }
}
