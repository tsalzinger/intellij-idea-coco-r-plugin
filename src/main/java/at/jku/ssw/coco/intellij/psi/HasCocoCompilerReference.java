package at.jku.ssw.coco.intellij.psi;

import com.intellij.psi.ContributedReferenceHost;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

public interface HasCocoCompilerReference extends ContributedReferenceHost {
    @Nullable
    PsiElement getIdent();
}