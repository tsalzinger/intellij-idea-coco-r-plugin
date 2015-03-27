package at.jku.ssw.coco.intellij.reference;

import at.jku.ssw.coco.intellij.CocoUtil;
import at.jku.ssw.coco.intellij.psi.CocoEnd;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CocoCompilerReference extends PsiReferenceBase<CocoEnd> implements PsiReference {

    public CocoCompilerReference(@NotNull CocoEnd element, TextRange textRange) {
        super(element, textRange);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        PsiElement ident = myElement.getIdent();
        if (ident == null) {
            return null;
        }
        return CocoUtil.findCompiler(myElement.getContainingFile(), ident.getText());
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        PsiElement resolve = resolve();
        return resolve != null ? new Object[]{resolve} : new Object[0];
    }
}