package at.jku.ssw.coco.intellij;

import at.jku.ssw.coco.intellij.psi.CocoCompiler;
import at.jku.ssw.coco.intellij.psi.CocoEnd;
import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

public class CocoReferenceContributor extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(PlatformPatterns.psiElement(CocoEnd.class),
                new PsiReferenceProvider() {
                    @NotNull
                    @Override
                    public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
                        CocoEnd cocoEnd = (CocoEnd) element;
                        PsiElement compilerIdent = cocoEnd.getIdent();
                        if (compilerIdent != null) {
                            CocoCompiler compiler = CocoUtil.findCompiler(element.getContainingFile(), compilerIdent.getText());

                            if (compiler != null) {

                                int startOffsetInParent = compilerIdent.getStartOffsetInParent();
                                TextRange range = new TextRange(startOffsetInParent, startOffsetInParent + compilerIdent.getTextLength());
                                return new PsiReference[]{new CocoCompilerReference(cocoEnd, range)};
                            }
                        }

                        return PsiReference.EMPTY_ARRAY;
                    }
                });
    }


}