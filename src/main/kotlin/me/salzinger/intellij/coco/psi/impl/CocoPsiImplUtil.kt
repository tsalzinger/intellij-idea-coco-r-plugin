package me.salzinger.intellij.coco.psi.impl

import com.intellij.navigation.ItemPresentation
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNameIdentifierOwner
import com.intellij.psi.PsiNamedElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import me.salzinger.intellij.coco.CocoIcons
import javax.swing.Icon

/**
 * @author Thomas Salzinger [tsalzinger@gmail.com](mailto:tsalzinger@gmail.com)
 */
object CocoPsiImplUtil {


    @JvmStatic
    fun getName(element: PsiNameIdentifierOwner): String? {
        val nameIdentifier = element.nameIdentifier
        return nameIdentifier?.text
    }

    @JvmStatic
    fun setName(element: PsiNameIdentifierOwner, newName: String): PsiElement {
        val nameIdentifier = element.nameIdentifier

        (nameIdentifier as? LeafPsiElement)?.replaceWithText(newName) ?: throw UnsupportedOperationException("Cannot rename element of type " + element.javaClass.simpleName)

        return element
    }

    @JvmStatic
    fun getNameIdentifier(element: PsiNamedElement): PsiElement? {
        val nameNode = element.node.findChildByType(me.salzinger.intellij.coco.psi.CocoTypes.IDENT)
        return nameNode?.psi
    }

    @JvmStatic
    fun getTextOffset(cocoCompiler: me.salzinger.intellij.coco.psi.CocoCompiler): Int {
        return cocoCompiler.startOffsetInParent + cocoCompiler.nameIdentifier!!.startOffsetInParent
    }

    @JvmStatic
    fun getPresentation(element: PsiNamedElement): ItemPresentation {
        return object : ItemPresentation {
            override fun getPresentableText(): String? {
                return element.name
            }

            override fun getLocationString(): String? {
                return element.containingFile.name
            }

            override fun getIcon(unused: Boolean): Icon? {
                return CocoIcons.FILE
            }
        }
    }

    @JvmStatic
    fun getPresentation(element: me.salzinger.intellij.coco.psi.CocoDirective): ItemPresentation {
        return object : ItemPresentation {
            override fun getPresentableText(): String? {
                return element.text
            }

            override fun getLocationString(): String? {
                return element.containingFile.name
            }

            override fun getIcon(unused: Boolean): Icon? {
                return CocoIcons.FILE
            }
        }
    }
}
