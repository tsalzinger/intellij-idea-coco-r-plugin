package at.scheinecker.intellij.coco.structure

import at.scheinecker.intellij.coco.psi.*
import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.ide.util.treeView.smartTree.SortableTreeElement
import com.intellij.ide.util.treeView.smartTree.TreeElement
import com.intellij.navigation.ItemPresentation
import com.intellij.navigation.NavigationItem
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNamedElement
import com.intellij.psi.util.PsiTreeUtil
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
        if (element is PsiNamedElement) {
            val name = element.name
            if (name != null) {
                return name
            }
        }

        return ""
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
                if (element is CocoFile) {
                    return presentation
                }

                if (element is CocoSetDecl) {
                    return if (element.getParent() is CocoCharacters) {
                        getPresentation(presentation.presentableText, "Character")
                    } else getPresentation(presentation.presentableText, "Pragma")

                }

                if (element is CocoTokenDecl) {
                    return getPresentation(presentation.presentableText, "Token")
                }

                if (element is CocoProduction) {
                    return getPresentation(presentation.presentableText, "Production")
                }

                if (element is CocoCompiler) {
                    return getPresentation(presentation.presentableText, "Compiler", presentation.getIcon(false))
                }

                if (element is CocoEnd) {
                    return getPresentation(presentation.presentableText, "End")
                }
            }
        }


        if (element is CocoCommentDecl) {
            return getPresentation(element.getText(), "Comment")
        }
        if (element is CocoParserSpecification) {
            return getPresentation("Productions")
        }
        if (element is CocoScannerSpecification) {
            return getPresentation("Scanner Specification")
        }
        if (element is CocoComments) {
            return getPresentation("Comments")
        }
        if (element is CocoCharacters) {
            return getPresentation("Characters")
        }
        if (element is CocoTokens) {
            return getPresentation("Tokens")
        }
        if (element is CocoPragmas) {
            return getPresentation("Pragmas")
        }

        throw IllegalArgumentException("Illegal element of type '" + element.javaClass.simpleName + "' for structure view")
    }

    override fun getChildren(): Array<TreeElement> {
        val treeElements = ArrayList<TreeElement>()
        if (element is CocoFile) {
            val compilers = PsiTreeUtil.getChildrenOfType(element, CocoCompiler::class.java)
            if (compilers != null) {
                for (compiler in compilers) {
                    treeElements.add(CocoStructureViewElement(compiler))
                }
            }
            val scannerSpecifications = PsiTreeUtil.getChildrenOfType(element, CocoScannerSpecification::class.java)
            if (scannerSpecifications != null) {
                for (scannerSpecification in scannerSpecifications) {
                    treeElements.add(CocoStructureViewElement(scannerSpecification))
                }
            }
            val parserSpecifications = PsiTreeUtil.getChildrenOfType(element, CocoParserSpecification::class.java)
            if (parserSpecifications != null) {
                for (parserSpecification in parserSpecifications) {
                    treeElements.add(CocoStructureViewElement(parserSpecification))
                }
            }
            val ends = PsiTreeUtil.getChildrenOfType(element, CocoEnd::class.java)
            if (ends != null) {
                for (end in ends) {
                    treeElements.add(CocoStructureViewElement(end))
                }
            }
        } else if (element is CocoScannerSpecification) {
            val scannerSpecification = element
            addToTreeElement(treeElements, scannerSpecification.comments)
            addToTreeElement(treeElements, scannerSpecification.characters)
            addToTreeElement(treeElements, scannerSpecification.tokens)
            addToTreeElement(treeElements, scannerSpecification.pragmas)
        } else if (element is CocoCharacters) {
            addToTreeElement(treeElements, element.setDeclList)
        } else if (element is CocoComments) {
            addToTreeElement(treeElements, element.commentDeclList)
        } else if (element is CocoTokens) {
            addToTreeElement(treeElements, element.tokenDeclList)
        } else if (element is CocoPragmas) {
            val pragmaDeclList = element.pragmaDeclList
            for (cocoPragmaDecl in pragmaDeclList) {
                addToTreeElement(treeElements, cocoPragmaDecl.tokenDecl)
            }
        } else if (element is CocoParserSpecification) {
            addToTreeElement(treeElements, element.productionList)
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
