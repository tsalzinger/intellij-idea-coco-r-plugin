package at.jku.ssw.coco.intellij.reference;

import at.jku.ssw.coco.intellij.CocoUtil;
import at.jku.ssw.coco.intellij.psi.CocoTokenExpr;
import at.jku.ssw.coco.intellij.psi.HasCocoTokenReference;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CocoTokenReference extends PsiReferenceBase<HasCocoTokenReference> implements PsiReference {

    public CocoTokenReference(@NotNull HasCocoTokenReference element, TextRange textRange) {
        super(element, textRange);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        return CocoUtil.findTokenDecl(myElement.getContainingFile(), myElement.getTokenReferenceName());
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return CocoUtil.findTokenDecls(myElement.getContainingFile())
                .stream()
                .map(it -> {
                    CocoTokenExpr tokenExpr = it.getTokenExpr();

                    LookupElementBuilder lookupElementBuilder = LookupElementBuilder.create(it)
                            .withTypeText("Token");

                    if (tokenExpr != null) {
                        return lookupElementBuilder.withTailText(" = " +tokenExpr.getText(), true);
                    }

                    return lookupElementBuilder;
                })
                .toArray();
    }
}