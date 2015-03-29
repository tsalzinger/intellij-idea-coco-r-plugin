package at.jku.ssw.coco.intellij.reference;

import at.jku.ssw.coco.intellij.psi.HasCocoCharacterReference;
import at.jku.ssw.coco.intellij.psi.HasCocoCompilerReference;
import at.jku.ssw.coco.intellij.psi.HasCocoProductionReference;
import at.jku.ssw.coco.intellij.psi.HasCocoTokenReference;
import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;
import com.intellij.util.ProcessingContext;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;

public class CocoReferenceContributor extends PsiReferenceContributor {
    @NotNull
    private TextRange getRelativeTextRange(@NotNull PsiElement element) {
        int startOffsetInParent = element.getStartOffsetInParent();
        return new TextRange(startOffsetInParent, startOffsetInParent + element.getTextLength());
    }

    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        register(registrar, HasCocoCompilerReference.class, CocoCompilerReference::new);
        register(registrar, HasCocoCharacterReference.class, CocoCharacterReference::new);
        register(registrar, HasCocoProductionReference.class, CocoProductionReference::new);
        register(registrar, HasCocoTokenReference.class, CocoTokenReference::new);
    }

    private <T extends PsiNameIdentifierOwner> void register(PsiReferenceRegistrar registrar, Class<T> hasIdentClass, BiFunction<T, TextRange, PsiReference> psiReferenceProvider) {
        registrar.registerReferenceProvider(PlatformPatterns.psiElement(hasIdentClass),
                new PsiReferenceProvider() {
                    @NotNull
                    @Override
                    @SuppressWarnings("unchecked")
                    public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
                        T hasIdent = (T) element;
                        PsiElement ident = hasIdent.getNameIdentifier();

                        if (ident != null) {
                            if (StringUtils.isNotBlank(hasIdent.getName())) {
                                return new PsiReference[]{psiReferenceProvider.apply(hasIdent, getRelativeTextRange(ident))};
                            }
                        }

                        return PsiReference.EMPTY_ARRAY;
                    }
                });
    }
}