package me.salzinger.intellij.coco.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.psi.ContributedReferenceHost
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceService

/**
 * Created by Thomas on 27/03/2015.
 */
abstract class CocoReferencingType(node: ASTNode) : CocoNamedElementBase(node), ContributedReferenceHost {

    override fun getReference(): PsiReference? {
        return references.firstOrNull()
    }

    override fun getReferences(): Array<PsiReference> {
        return PsiReferenceService.getService().getContributedReferences(this)
    }
}
