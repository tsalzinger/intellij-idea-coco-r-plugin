package at.jku.ssw.coco.intellij.reference;

import at.jku.ssw.coco.intellij.psi.HasCocoCharacterReference;
import at.jku.ssw.coco.intellij.psi.HasCocoCompilerReference;
import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;
import com.intellij.util.ProcessingContext;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

public class CocoReferenceContributor extends PsiReferenceContributor {
    private TextRange getRelativeTextRange(PsiElement element) {
        int startOffsetInParent = element.getStartOffsetInParent();
        return new TextRange(startOffsetInParent, startOffsetInParent + element.getTextLength());
    }

    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(PlatformPatterns.psiElement(HasCocoCompilerReference.class),
                new PsiReferenceProvider() {
                    @NotNull
                    @Override
                    public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
                        HasCocoCompilerReference cocoEnd = (HasCocoCompilerReference) element;
                        PsiElement compilerIdent = cocoEnd.getIdent();
                        if (compilerIdent != null && StringUtils.isNotBlank(compilerIdent.getText())) {
                            return new PsiReference[]{new CocoCompilerReference(cocoEnd, getRelativeTextRange(compilerIdent))};
                        }

                        return PsiReference.EMPTY_ARRAY;
                    }
                });

        registrar.registerReferenceProvider(PlatformPatterns.psiElement(HasCocoCharacterReference.class),
                new PsiReferenceProvider() {
                    @NotNull
                    @Override
                    public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
                        HasCocoCharacterReference hasCocoCharacterReference = (HasCocoCharacterReference) element;
                        PsiElement ident = hasCocoCharacterReference.getIdent();

                        if (ident != null) {
                            String referenceName = ident.getText();
                            if (StringUtils.isNotBlank(referenceName)) {
                                return new PsiReference[]{new CocoCharacterReference(hasCocoCharacterReference, getRelativeTextRange(ident))};
                            }
                        }

                        return PsiReference.EMPTY_ARRAY;
                    }
                });
    }
}