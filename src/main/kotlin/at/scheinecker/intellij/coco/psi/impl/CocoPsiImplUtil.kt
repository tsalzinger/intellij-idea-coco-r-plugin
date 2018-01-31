package at.scheinecker.intellij.coco.psi.impl

import at.scheinecker.intellij.coco.CocoIcons
import at.scheinecker.intellij.coco.psi.*
import com.intellij.navigation.ItemPresentation
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNameIdentifierOwner
import com.intellij.psi.PsiNamedElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import javax.swing.Icon

/**
 * @author Thomas Scheinecker [tscheinecker@gmail.com](mailto:tscheinecker@gmail.com)
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
        val nameNode = element.node.findChildByType(CocoTypes.IDENT)
        return nameNode?.psi
    }

    @JvmStatic
    fun getTextOffset(cocoCompiler: CocoCompiler): Int {
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
    fun getPresentation(element: CocoDirectiveElement): ItemPresentation {
        val text = element.text
        val name = when (element) {
            is CocoPackageDirective -> "package ${text.substringAfter('=', "??")}"
            is CocoCheckEofDirective -> (if (text.contains("true", true)) "" else "don't ") + "check EOF"
            is CocoAnyDirective -> text
            else -> null
        }

        return object : ItemPresentation {
            override fun getPresentableText(): String? {
                return name
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
