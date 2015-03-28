package at.jku.ssw.coco.intellij.reference;

import at.jku.ssw.coco.intellij.CocoUtil;
import at.jku.ssw.coco.intellij.psi.CocoTokenDecl;
import at.jku.ssw.coco.intellij.psi.HasCocoTokenReference;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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
        List<CocoTokenDecl> characterDeclarations = CocoUtil.findTokenDecls(myElement.getContainingFile());
        return characterDeclarations.toArray();
    }
}