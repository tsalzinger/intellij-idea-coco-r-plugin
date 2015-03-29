package at.jku.ssw.coco.intellij.structure;

import at.jku.ssw.coco.intellij.psi.*;
import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.SortableTreeElement;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thomas on 29/03/2015.
 */
public class CocoStructureViewElement implements StructureViewTreeElement, SortableTreeElement {
    private final PsiElement element;

    public CocoStructureViewElement(PsiElement element) {
        this.element = element;
    }

    @Override
    public Object getValue() {
        return element;
    }

    @Override
    public void navigate(boolean requestFocus) {
        if (element instanceof NavigationItem) {
            ((NavigationItem) element).navigate(requestFocus);
        }
    }

    @Override
    public boolean canNavigate() {
        return element instanceof NavigationItem &&
                ((NavigationItem) element).canNavigate();
    }

    @Override
    public boolean canNavigateToSource() {
        return element instanceof NavigationItem &&
                ((NavigationItem) element).canNavigateToSource();
    }

    @NotNull
    @Override
    public String getAlphaSortKey() {
        if (element instanceof PsiNamedElement) {
            String name = ((PsiNamedElement) element).getName();
            if (name != null) {
                return name;
            }
        }

        return "";
    }

    private ItemPresentation getPresentation(@Nullable final String text) {
        return getPresentation(text, null);
    }

    private ItemPresentation getPresentation(@Nullable final String text, @Nullable final String type) {
        return getPresentation(text, type, null);
    }

    private ItemPresentation getPresentation(@Nullable final String text, @Nullable final String type, @Nullable final Icon icon) {
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                return text;
            }

            @Nullable
            @Override
            public String getLocationString() {
                return type;
            }

            @Nullable
            @Override
            public Icon getIcon(boolean unused) {
                return icon;
            }
        };
    }

    @NotNull
    @Override
    public ItemPresentation getPresentation() {

        final String text;
        final String type;

        if (element instanceof NavigationItem) {
            ItemPresentation presentation = ((NavigationItem) element).getPresentation();
            if (presentation != null) {
                if (element instanceof CocoFile) {
                    return presentation;
                }

                if (element instanceof CocoSetDecl) {
                    if (element.getParent() instanceof CocoCharacters) {
                        return getPresentation(presentation.getPresentableText(), "Character");
                    }

                    return getPresentation(presentation.getPresentableText(), "Pragma");
                }

                if (element instanceof CocoTokenDecl) {
                    return getPresentation(presentation.getPresentableText(), "Token");
                }

                if (element instanceof CocoProduction) {
                    return getPresentation(presentation.getPresentableText(), "Production");
                }

                if (element instanceof CocoCompiler) {
                    return getPresentation(presentation.getPresentableText(), "Compiler", presentation.getIcon(false));
                }

                if (element instanceof CocoEnd) {
                    return getPresentation(presentation.getPresentableText(), "End");
                }
            }
        }


        if (element instanceof CocoCommentDecl) {
            return getPresentation(element.getText(), "Comment");
        }
        if (element instanceof CocoParserSpecification) {
            return getPresentation("Productions");
        }
        if (element instanceof CocoScannerSpecification) {
            return getPresentation("Scanner Specification");
        }
        if (element instanceof CocoComments) {
            return getPresentation("Comments");
        }
        if (element instanceof CocoCharacters) {
            return getPresentation("Characters");
        }
        if (element instanceof CocoTokens) {
            return getPresentation("Tokens");
        }
        if (element instanceof CocoPragmas) {
            return getPresentation("Pragmas");
        }

        throw new IllegalArgumentException("Illegal element of type '" + element.getClass().getSimpleName() + "' for structure view");
    }

    @NotNull
    @Override
    public TreeElement[] getChildren() {
        List<TreeElement> treeElements = new ArrayList<>();
        if (element instanceof CocoFile) {
            CocoCompiler[] compilers = PsiTreeUtil.getChildrenOfType(element, CocoCompiler.class);
            if (compilers != null) {
                for (CocoCompiler compiler : compilers) {
                    treeElements.add(new CocoStructureViewElement(compiler));
                }
            }
            CocoScannerSpecification[] scannerSpecifications = PsiTreeUtil.getChildrenOfType(element, CocoScannerSpecification.class);
            if (scannerSpecifications != null) {
                for (CocoScannerSpecification scannerSpecification : scannerSpecifications) {
                    treeElements.add(new CocoStructureViewElement(scannerSpecification));
                }
            }
            CocoParserSpecification[] parserSpecifications = PsiTreeUtil.getChildrenOfType(element, CocoParserSpecification.class);
            if (parserSpecifications != null) {
                for (CocoParserSpecification parserSpecification : parserSpecifications) {
                    treeElements.add(new CocoStructureViewElement(parserSpecification));
                }
            }
            CocoEnd[] ends = PsiTreeUtil.getChildrenOfType(element, CocoEnd.class);
            if (ends != null) {
                for (CocoEnd end : ends) {
                    treeElements.add(new CocoStructureViewElement(end));
                }
            }
        } else if (element instanceof CocoScannerSpecification) {
            CocoScannerSpecification scannerSpecification = (CocoScannerSpecification) element;
            addToTreeElement(treeElements, scannerSpecification.getComments());
            addToTreeElement(treeElements, scannerSpecification.getCharacters());
            addToTreeElement(treeElements, scannerSpecification.getTokens());
            addToTreeElement(treeElements, scannerSpecification.getPragmas());
        } else if (element instanceof CocoCharacters) {
            addToTreeElement(treeElements, ((CocoCharacters) element).getSetDeclList());
        } else if (element instanceof CocoComments) {
            addToTreeElement(treeElements, ((CocoComments) element).getCommentDeclList());
        } else if (element instanceof CocoTokens) {
            addToTreeElement(treeElements, ((CocoTokens) element).getTokenDeclList());
        } else if (element instanceof CocoPragmas) {
            List<CocoPragmaDecl> pragmaDeclList = ((CocoPragmas) element).getPragmaDeclList();
            for (CocoPragmaDecl cocoPragmaDecl : pragmaDeclList) {
                addToTreeElement(treeElements, cocoPragmaDecl.getTokenDecl());
            }
        } else if (element instanceof CocoParserSpecification) {
            addToTreeElement(treeElements, ((CocoParserSpecification) element).getProductionList());
        }
        return treeElements.toArray(new TreeElement[treeElements.size()]);
    }

    private void addToTreeElement(@NotNull List<TreeElement> treeElements, @Nullable PsiElement element) {
        if (element != null) {
            treeElements.add(new CocoStructureViewElement(element));
        }
    }

    private void addToTreeElement(@NotNull List<TreeElement> treeElements, @NotNull List<? extends PsiElement> elements) {
        for (PsiElement psiElement : elements) {
            addToTreeElement(treeElements, psiElement);
        }
    }
}
