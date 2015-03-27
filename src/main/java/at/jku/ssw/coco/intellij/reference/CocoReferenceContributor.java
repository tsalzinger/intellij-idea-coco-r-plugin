package at.jku.ssw.coco.intellij.reference;

import at.jku.ssw.coco.intellij.CocoUtil;
import at.jku.ssw.coco.intellij.psi.CocoBasicSet;
import at.jku.ssw.coco.intellij.psi.CocoCompiler;
import at.jku.ssw.coco.intellij.psi.CocoEnd;
import at.jku.ssw.coco.intellij.psi.CocoSetDecl;
import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;
import com.intellij.util.ProcessingContext;
import org.apache.commons.lang.StringUtils;
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

        registrar.registerReferenceProvider(PlatformPatterns.psiElement(CocoBasicSet.class),
                new PsiReferenceProvider() {
                    @NotNull
                    @Override
                    public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
                        CocoBasicSet cocoBasicSet = (CocoBasicSet) element;
                        PsiElement ident = cocoBasicSet.getIdent();

                        if (ident != null) {
                            String referenceName = ident.getText();
                            if (StringUtils.isNotBlank(referenceName)) {
                                CocoSetDecl characterDeclaration = CocoUtil.findCharacterDeclaration(element.getContainingFile(), referenceName);
                                if (characterDeclaration != null) {
                                    TextRange range = new TextRange(0, ident.getTextLength());
                                    return new PsiReference[]{new CocoCharacterReference(ident, range)};
                                }
                            }
                        }

                        return PsiReference.EMPTY_ARRAY;
                    }
                });
    }
}