package at.jku.ssw.coco.intellij;

import at.jku.ssw.coco.intellij.psi.CocoNamedElement;
import com.intellij.lang.refactoring.RefactoringSupportProvider;
import com.intellij.psi.PsiElement;

/**
 * Created by Thomas on 29/03/2015.
 */
public class CocoRefactoringProvider extends RefactoringSupportProvider {
    @Override
    public boolean isMemberInplaceRenameAvailable(PsiElement element, PsiElement context) {
        return element instanceof CocoNamedElement;
    }
}
