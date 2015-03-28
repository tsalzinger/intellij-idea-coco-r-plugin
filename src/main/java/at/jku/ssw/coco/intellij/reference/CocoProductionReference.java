package at.jku.ssw.coco.intellij.reference;

import at.jku.ssw.coco.intellij.CocoUtil;
import at.jku.ssw.coco.intellij.psi.CocoProduction;
import at.jku.ssw.coco.intellij.psi.HasCocoProductionReference;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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
        List<CocoProduction> characterDeclarations = CocoUtil.findProductions(myElement.getContainingFile());
        return characterDeclarations.toArray();
    }
}