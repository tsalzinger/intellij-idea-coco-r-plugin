package me.salzinger.intellij.coco.structure

import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.ide.util.treeView.smartTree.SortableTreeElement
import com.intellij.ide.util.treeView.smartTree.TreeElement
import com.intellij.navigation.ItemPresentation
import com.intellij.navigation.NavigationItem
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNamedElement
import me.salzinger.intellij.coco.psi.CocoCharacters
import me.salzinger.intellij.coco.psi.CocoCocoInjectorHost
import me.salzinger.intellij.coco.psi.CocoCommentDecl
import me.salzinger.intellij.coco.psi.CocoComments
import me.salzinger.intellij.coco.psi.CocoCompiler
import me.salzinger.intellij.coco.psi.CocoDirectiveElement
import me.salzinger.intellij.coco.psi.CocoDirectives
import me.salzinger.intellij.coco.psi.CocoEnd
import me.salzinger.intellij.coco.psi.CocoFile
import me.salzinger.intellij.coco.psi.CocoParserSpecification
import me.salzinger.intellij.coco.psi.CocoPragmas
import me.salzinger.intellij.coco.psi.CocoProduction
import me.salzinger.intellij.coco.psi.CocoScannerSpecification
import me.salzinger.intellij.coco.psi.CocoSetDecl
import me.salzinger.intellij.coco.psi.CocoTokenDecl
import me.salzinger.intellij.coco.psi.CocoTokens
import me.salzinger.intellij.coco.psi.findChild
import me.salzinger.intellij.coco.psi.findChildren
import javax.swing.Icon

/**
 * Created by Thomas on 29/03/2015.
 */
class CocoStructureViewElement(private val element: PsiElement) : StructureViewTreeElement, SortableTreeElement {

    override fun getValue(): Any {
        return element
    }

    override fun navigate(requestFocus: Boolean) {
        if (element is NavigationItem) {
            element.navigate(requestFocus)
        }
    }

    override fun canNavigate(): Boolean {
        return element is NavigationItem && element.canNavigate()
    }

    override fun canNavigateToSource(): Boolean {
        return element is NavigationItem && element.canNavigateToSource()
    }

    override fun getAlphaSortKey(): String {
        return when (element) {
            is CocoCompiler -> "1"
            is CocoScannerSpecification -> "2"
            is CocoParserSpecification -> "3"
            is CocoEnd -> "4"
            is PsiNamedElement -> element.name.orEmpty()
            else -> element.text
        }
    }

    override fun getPresentation(): ItemPresentation {
        if (element is NavigationItem) {
            val presentation = getPresentation(element)
            if (presentation != null) {
                return presentation
            }
        }

        return when (element) {
            is CocoDirectives -> CocoItemPresentation("Directives")
            is CocoCommentDecl -> CocoItemPresentation(element.getText(), "Comment")
            is CocoParserSpecification -> CocoItemPresentation("Productions (${element.productionList.size})")
            is CocoScannerSpecification -> CocoItemPresentation("Scanner Specification")
            is CocoComments -> CocoItemPresentation("Comments (${element.commentDeclList.size})")
            is CocoCharacters -> CocoItemPresentation("Characters (${element.setDeclList.size})")
            is CocoTokens -> CocoItemPresentation("Tokens (${element.tokenDeclList.size})")
            is CocoPragmas -> CocoItemPresentation("Pragmas (${element.pragmaDeclList.size})")
            else -> {
                throw IllegalArgumentException(
                    "Illegal element of type '${element.javaClass.simpleName}' for structure view"
                )
            }
        }
    }

    override fun getChildren(): Array<TreeElement> {
        val treeElements = mutableListOf<TreeElement>()
        when (element) {
            is CocoFile -> {
                val injectionHost: CocoCocoInjectorHost? = element.findChild()
                val cocoDirectives: CocoDirectives? = element.findChild()
                val cocoDirectiveElements: List<CocoDirectiveElement> = cocoDirectives.findChildren()

                if (cocoDirectives != null && cocoDirectiveElements.isNotEmpty()) {
                    treeElements.addIfNotNull(cocoDirectives)
                }

                treeElements.addAll(injectionHost.findChildren<CocoCompiler>())
                treeElements.addAll(injectionHost.findChildren<CocoScannerSpecification>())
                treeElements.addAll(injectionHost.findChildren<CocoParserSpecification>())
                treeElements.addAll(injectionHost.findChildren<CocoEnd>())
            }
            is CocoScannerSpecification -> {
                val scannerSpecification = element
                treeElements
                    .addIfNotNull(scannerSpecification.comments)
                    .addIfNotNull(scannerSpecification.characters)
                    .addIfNotNull(scannerSpecification.tokens)
                    .addIfNotNull(scannerSpecification.pragmas)
            }
            is CocoCharacters -> {
                treeElements.addAll(element.setDeclList)
            }
            is CocoComments -> {
                treeElements.addAll(element.commentDeclList)
            }
            is CocoTokens -> {
                treeElements.addAll(element.tokenDeclList)
            }
            is CocoPragmas -> {
                treeElements.addAll(element.pragmaDeclList.map { it.tokenDecl })
            }
            is CocoParserSpecification -> {
                treeElements.addAll(element.productionList)
            }
            is CocoDirectives -> {
                treeElements.addAll(element.directiveList)
            }
        }
        return treeElements.toTypedArray()
    }

    private fun <T : PsiElement> MutableList<TreeElement>.addIfNotNull(psiElement: T?): MutableList<TreeElement> {
        if (psiElement != null) {
            add(CocoStructureViewElement(psiElement))
        }
        return this
    }

    private fun <T : PsiElement> MutableList<TreeElement>.addAll(psiElements: Collection<T>): MutableList<TreeElement> {
        addAll(psiElements.map(::CocoStructureViewElement))
        return this
    }

    private fun getPresentation(element: NavigationItem): ItemPresentation? {
        val presentation = element.presentation
        return if (presentation != null) {
            when (element) {
                is CocoFile -> presentation
                is CocoDirectiveElement -> CocoItemPresentation(presentation.presentableText, "Directive")
                is CocoSetDecl -> CocoItemPresentation(
                    presentation.presentableText,
                    when (element.parent) {
                        is CocoCharacters -> "Character"
                        is CocoPragmas -> "Pragma"
                        else -> null
                    }
                )
                is CocoTokenDecl -> CocoItemPresentation(presentation.presentableText, "Token")
                is CocoProduction -> CocoItemPresentation(presentation.presentableText, "Production")
                is CocoCompiler -> CocoItemPresentation(
                    text = presentation.presentableText,
                    type = "Compiler",
                    icon = presentation.getIcon(false)
                )
                is CocoEnd -> CocoItemPresentation(presentation.presentableText, "End")
                else -> presentation
            }
        } else {
            null
        }
    }

    private data class CocoItemPresentation(
        val text: String?,
        val type: String? = null,
        val icon: Icon? = null,
    ) : ItemPresentation {
        override fun getPresentableText(): String? {
            return text
        }

        override fun getLocationString(): String? {
            return type
        }

        override fun getIcon(unused: Boolean): Icon? {
            return icon
        }
    }
}
