package at.jku.ssw.coco.intellij.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

public interface HasIdent extends PsiElement {
    @Nullable
    PsiElement getIdent();

    @Nullable
    String getIdentText();
}