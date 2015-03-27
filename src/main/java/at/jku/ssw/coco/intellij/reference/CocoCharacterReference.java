package at.jku.ssw.coco.intellij.reference;

import at.jku.ssw.coco.intellij.CocoUtil;
import at.jku.ssw.coco.intellij.psi.CocoSetDecl;
import at.jku.ssw.coco.intellij.psi.HasCocoCharacterReference;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CocoCharacterReference extends PsiReferenceBase<HasCocoCharacterReference> implements PsiReference {

    public CocoCharacterReference(@NotNull HasCocoCharacterReference element, TextRange textRange) {
        super(element, textRange);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        return CocoUtil.findCharacterDeclaration(myElement.getContainingFile(), myElement.getCharacterReferenceName());
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        List<CocoSetDecl> characterDeclarations = CocoUtil.findCharacterDeclarations(myElement.getContainingFile());
        return characterDeclarations.toArray();
    }
}