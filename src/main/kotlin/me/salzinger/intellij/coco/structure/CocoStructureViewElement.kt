package me.salzinger.intellij.coco.structure

import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.ide.util.treeView.smartTree.SortableTreeElement
import com.intellij.ide.util.treeView.smartTree.TreeElement
import com.intellij.navigation.ItemPresentation
import com.intellij.navigation.NavigationItem
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNamedElement
import com.intellij.psi.util.PsiTreeUtil
import me.salzinger.intellij.coco.psi.CocoDirectiveElement
import me.salzinger.intellij.coco.psi.CocoFile
import java.util.*
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
            (element as NavigationItem).navigate(requestFocus)
        }
    }

    override fun canNavigate(): Boolean {
        return element is NavigationItem && (element as NavigationItem).canNavigate()
    }

    override fun canNavigateToSource(): Boolean {
        return element is NavigationItem && (element as NavigationItem).canNavigateToSource()
    }

    override fun getAlphaSortKey(): String {

        when (element) {
            is me.salzinger.intellij.coco.psi.CocoCompiler -> return "1"
            is me.salzinger.intellij.coco.psi.CocoScannerSpecification -> return "2"
            is me.salzinger.intellij.coco.psi.CocoParserSpecification -> return "3"
            is me.salzinger.intellij.coco.psi.CocoEnd -> return "4"
            is PsiNamedElement -> return element.name.orEmpty()
        }

        return element.text
    }

    private fun getPresentation(text: String?, type: String? = null, icon: Icon? = null): ItemPresentation {
        return object : ItemPresentation {
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

    override fun getPresentation(): ItemPresentation {

        if (element is NavigationItem) {
            val presentation = (element as NavigationItem).presentation
            if (presentation != null) {
                when (element) {
                    is CocoFile -> {
                        return presentation
                    }
                    is CocoDirectiveElement -> {
                        return getPresentation(presentation.presentableText, "Directive")
                    }
                    is me.salzinger.intellij.coco.psi.CocoSetDecl -> {
                        return if (element.getParent() is me.salzinger.intellij.coco.psi.CocoCharacters) {
                            getPresentation(presentation.presentableText, "Character")
                        } else getPresentation(presentation.presentableText, "Pragma")
                    }
                    is me.salzinger.intellij.coco.psi.CocoTokenDecl -> {
                        return getPresentation(presentation.presentableText, "Token")
                    }
                    is me.salzinger.intellij.coco.psi.CocoProduction -> {
                        return getPresentation(presentation.presentableText, "Production")
                    }
                    is me.salzinger.intellij.coco.psi.CocoCompiler -> {
                        return getPresentation(presentation.presentableText, "Compiler", presentation.getIcon(false))
                    }
                    is me.salzinger.intellij.coco.psi.CocoEnd -> {
                        return getPresentation(presentation.presentableText, "End")
                    }
                }
            }
        }

        when (element) {
            is me.salzinger.intellij.coco.psi.CocoDirectives -> {
                return getPresentation("Directives")
            }
        }

        if (element is me.salzinger.intellij.coco.psi.CocoCommentDecl) {
            return getPresentation(element.getText(), "Comment")
        }
        if (element is me.salzinger.intellij.coco.psi.CocoParserSpecification) {
            return getPresentation("Productions (${element.productionList.size})")
        }
        if (element is me.salzinger.intellij.coco.psi.CocoScannerSpecification) {
            return getPresentation("Scanner Specification")
        }
        if (element is me.salzinger.intellij.coco.psi.CocoComments) {
            return getPresentation("Comments (${element.commentDeclList.size})")
        }
        if (element is me.salzinger.intellij.coco.psi.CocoCharacters) {
            return getPresentation("Characters (${element.setDeclList.size})")
        }
        if (element is me.salzinger.intellij.coco.psi.CocoTokens) {
            return getPresentation("Tokens (${element.tokenDeclList.size})")
        }
        if (element is me.salzinger.intellij.coco.psi.CocoPragmas) {
            return getPresentation("Pragmas (${element.pragmaDeclList.size})")
        }

        throw IllegalArgumentException("Illegal element of type '" + element.javaClass.simpleName + "' for structure view")
    }

    override fun getChildren(): Array<TreeElement> {
        val treeElements = ArrayList<TreeElement>()
        if (element is CocoFile) {
            val injectionHost = PsiTreeUtil.getChildOfType(element, me.salzinger.intellij.coco.psi.CocoCocoInjectorHost::class.java)

            val cocoDirectives = PsiTreeUtil.getChildOfType(element, me.salzinger.intellij.coco.psi.CocoDirectives::class.java)
            if (cocoDirectives != null && PsiTreeUtil.getChildrenOfType(cocoDirectives, CocoDirectiveElement::class.java) != null) {
                treeElements.add(CocoStructureViewElement(cocoDirectives))
            }

            val compilers = PsiTreeUtil.getChildrenOfType(injectionHost, me.salzinger.intellij.coco.psi.CocoCompiler::class.java)
            if (compilers != null) {
                for (compiler in compilers) {
                    treeElements.add(CocoStructureViewElement(compiler))
                }
            }
            val scannerSpecifications = PsiTreeUtil.getChildrenOfType(injectionHost, me.salzinger.intellij.coco.psi.CocoScannerSpecification::class.java)
            if (scannerSpecifications != null) {
                for (scannerSpecification in scannerSpecifications) {
                    treeElements.add(CocoStructureViewElement(scannerSpecification))
                }
            }
            val parserSpecifications = PsiTreeUtil.getChildrenOfType(injectionHost, me.salzinger.intellij.coco.psi.CocoParserSpecification::class.java)
            if (parserSpecifications != null) {
                for (parserSpecification in parserSpecifications) {
                    treeElements.add(CocoStructureViewElement(parserSpecification))
                }
            }
            val ends = PsiTreeUtil.getChildrenOfType(injectionHost, me.salzinger.intellij.coco.psi.CocoEnd::class.java)
            if (ends != null) {
                for (end in ends) {
                    treeElements.add(CocoStructureViewElement(end))
                }
            }
        } else if (element is me.salzinger.intellij.coco.psi.CocoScannerSpecification) {
            val scannerSpecification = element
            addToTreeElement(treeElements, scannerSpecification.comments)
            addToTreeElement(treeElements, scannerSpecification.characters)
            addToTreeElement(treeElements, scannerSpecification.tokens)
            addToTreeElement(treeElements, scannerSpecification.pragmas)
        } else if (element is me.salzinger.intellij.coco.psi.CocoCharacters) {
            addToTreeElement(treeElements, element.setDeclList)
        } else if (element is me.salzinger.intellij.coco.psi.CocoComments) {
            addToTreeElement(treeElements, element.commentDeclList)
        } else if (element is me.salzinger.intellij.coco.psi.CocoTokens) {
            addToTreeElement(treeElements, element.tokenDeclList)
        } else if (element is me.salzinger.intellij.coco.psi.CocoPragmas) {
            val pragmaDeclList = element.pragmaDeclList
            for (cocoPragmaDecl in pragmaDeclList) {
                addToTreeElement(treeElements, cocoPragmaDecl.tokenDecl)
            }
        } else if (element is me.salzinger.intellij.coco.psi.CocoParserSpecification) {
            addToTreeElement(treeElements, element.productionList)
        } else if (element is me.salzinger.intellij.coco.psi.CocoDirectives) {
            addToTreeElement(treeElements, element.directiveList)
        }
        return treeElements.toTypedArray()
    }

    private fun addToTreeElement(treeElements: MutableList<TreeElement>, element: PsiElement?) {
        if (element != null) {
            treeElements.add(CocoStructureViewElement(element))
        }
    }

    private fun addToTreeElement(treeElements: MutableList<TreeElement>, elements: List<PsiElement>) {
        for (psiElement in elements) {
            addToTreeElement(treeElements, psiElement)
        }
    }
}
