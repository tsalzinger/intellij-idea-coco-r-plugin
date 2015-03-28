package at.jku.ssw.coco.intellij.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import org.jetbrains.annotations.Nullable;

public interface CocoNamedElement extends PsiNameIdentifierOwner {
    @Nullable
    PsiElement getIdent();
}