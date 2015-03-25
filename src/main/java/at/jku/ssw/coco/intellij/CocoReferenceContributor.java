package at.jku.ssw.coco.intellij;

import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

public class CocoReferenceContributor extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(PlatformPatterns.psiElement(PsiLiteralExpression.class),
                new PsiReferenceProvider() {
                    @NotNull
                    @Override
                    public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
                        PsiLiteralExpression literalExpression = (PsiLiteralExpression) element;
                        Object value = literalExpression.getValue();

                        if (value != null && value instanceof String) {
                            String text = (String) value;
                            if (text.startsWith("coco:")) {
                                return new PsiReference[]{new CocoCompilerReference(element, new TextRange(6, text.length() + 1))};
                            }

                        }
                        return PsiReference.EMPTY_ARRAY;
                    }
                }

        );
    }


}