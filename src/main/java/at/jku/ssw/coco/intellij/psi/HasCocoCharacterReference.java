package at.jku.ssw.coco.intellij.psi;

import com.intellij.psi.ContributedReferenceHost;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

public interface HasCocoCharacterReference extends ContributedReferenceHost {
    @Nullable
    PsiElement getIdent();

    @Nullable
    String getCharacterReferenceName();
}