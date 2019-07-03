package me.salzinger.intellij.coco.reference

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNameIdentifierOwner
import com.intellij.psi.PsiReferenceBase
import com.intellij.util.IncorrectOperationException
import me.salzinger.intellij.coco.psi.impl.CocoPsiImplUtil

/**
 * Created by Thomas on 29/03/2015.
 */
abstract class AbstractRenamableReference<T : PsiNameIdentifierOwner>(element: T, rangeInElement: TextRange) : PsiReferenceBase<T>(element, rangeInElement) {

    @Throws(IncorrectOperationException::class)
    override fun handleElementRename(newElementName: String): PsiElement {
        return CocoPsiImplUtil.setName(myElement, newElementName)
    }
}
