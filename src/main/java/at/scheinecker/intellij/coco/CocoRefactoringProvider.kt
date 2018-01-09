package at.scheinecker.intellij.coco

import at.scheinecker.intellij.coco.psi.CocoNamedElement
import com.intellij.lang.refactoring.RefactoringSupportProvider
import com.intellij.psi.PsiElement

/**
 * Created by Thomas on 29/03/2015.
 */
class CocoRefactoringProvider : RefactoringSupportProvider() {
    override fun isMemberInplaceRenameAvailable(element: PsiElement, context: PsiElement?): Boolean {
        return element is CocoNamedElement
    }
}
