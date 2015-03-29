package at.jku.ssw.coco.intellij.reference;

import at.jku.ssw.coco.intellij.psi.impl.CocoPsiImplUtil;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.util.IncorrectOperationException;

/**
 * Created by Thomas on 29/03/2015.
 */
public abstract class AbstractRenamableReference<T extends PsiNameIdentifierOwner> extends PsiReferenceBase<T> {
    public AbstractRenamableReference(T element, TextRange rangeInElement) {
        super(element, rangeInElement);
    }

    @Override
    public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
        return CocoPsiImplUtil.setName(myElement, newElementName);
    }
}
