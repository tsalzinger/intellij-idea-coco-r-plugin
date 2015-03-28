package at.jku.ssw.coco.intellij.reference;

import at.jku.ssw.coco.intellij.CocoUtil;
import at.jku.ssw.coco.intellij.psi.CocoFormalAttributes;
import at.jku.ssw.coco.intellij.psi.HasCocoProductionReference;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CocoProductionReference extends PsiReferenceBase<HasCocoProductionReference> implements PsiReference {

    public CocoProductionReference(@NotNull HasCocoProductionReference element, TextRange textRange) {
        super(element, textRange);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        return CocoUtil.findProduction(myElement.getContainingFile(), myElement.getProductionReferenceName());
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return CocoUtil.findProductions(myElement.getContainingFile())
                .stream()
                .map(it -> {
                    CocoFormalAttributes formalAttributes = it.getFormalAttributes();
                    LookupElementBuilder lookupElementBuilder = LookupElementBuilder.create(it)
                            .withTypeText("Production");

                    if (formalAttributes != null) {
                        return lookupElementBuilder.withTailText(formalAttributes.getText(), true);
                    }

                    return lookupElementBuilder;
                })
                .toArray();
    }
}