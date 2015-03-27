package at.jku.ssw.coco.intellij.reference;

import at.jku.ssw.coco.intellij.CocoUtil;
import at.jku.ssw.coco.intellij.psi.CocoSetDecl;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CocoCharacterReference extends PsiReferenceBase<PsiElement> implements PsiReference {

    public CocoCharacterReference(@NotNull PsiElement element, TextRange textRange) {
        super(element, textRange);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        String name = myElement.getText();

        if (myElement instanceof PsiNamedElement) {
            name = ((PsiNamedElement) myElement).getName();
        }

        return CocoUtil.findCharacterDeclaration(myElement.getContainingFile(), name);
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        List<CocoSetDecl> characterDeclarations = CocoUtil.findCharacterDeclarations(myElement.getContainingFile());
        return characterDeclarations.toArray();
    }
}