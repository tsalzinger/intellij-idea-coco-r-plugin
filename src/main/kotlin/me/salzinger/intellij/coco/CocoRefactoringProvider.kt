package me.salzinger.intellij.coco

import com.intellij.lang.refactoring.RefactoringSupportProvider
import com.intellij.psi.PsiElement
import me.salzinger.intellij.coco.psi.CocoNamedElement

/**
 * Created by Thomas on 29/03/2015.
 */
class CocoRefactoringProvider : RefactoringSupportProvider() {
    override fun isMemberInplaceRenameAvailable(element: PsiElement, context: PsiElement?): Boolean {
        return element is CocoNamedElement
    }
}
