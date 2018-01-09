package at.scheinecker.intellij.coco.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.ContributedReferenceHost
import com.intellij.psi.PsiNameIdentifierOwner
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceService

/**
 * Created by Thomas on 27/03/2015.
 */
abstract class CocoReferencingType(node: ASTNode) : ASTWrapperPsiElement(node), ContributedReferenceHost, PsiNameIdentifierOwner {

    override fun getReference(): PsiReference? {
        return references.firstOrNull()
    }

    override fun getReferences(): Array<PsiReference> {
        return PsiReferenceService.getService().getContributedReferences(this)
    }
}
