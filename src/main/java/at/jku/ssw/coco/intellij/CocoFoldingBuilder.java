package at.jku.ssw.coco.intellij;

import at.jku.ssw.coco.intellij.psi.*;
import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilderEx;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.FoldingGroup;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class CocoFoldingBuilder extends FoldingBuilderEx {

    private final String COCO_IMPORTS = "coco.imports";

    private static final Map<Class<? extends PsiElement>, String> foldingGroups;

    static {
        foldingGroups = new HashMap<>();

        foldingGroups.put(CocoImports.class, "coco.imports");
        foldingGroups.put(CocoGlobalFieldsAndMethods.class, "coco.globals");
        foldingGroups.put(CocoCharacters.class, "coco.characters");
        foldingGroups.put(CocoComments.class, "coco.comments");
        foldingGroups.put(CocoTokens.class, "coco.tokens");
        foldingGroups.put(CocoPragmas.class, "coco.pragmas");
        foldingGroups.put(CocoTokenDecl.class, "coco.tokenDeclaration");
        foldingGroups.put(CocoPragmaDecl.class, "coco.pragmaDeclaration");
        foldingGroups.put(CocoParserSpecification.class, "coco.parserSpecification");
        foldingGroups.put(CocoSemAction.class, "coco.semanticAction");
    }

    @NotNull
    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement root, @NotNull Document document, boolean quick) {

        List<FoldingDescriptor> descriptors = new ArrayList<>();

        addAll(descriptors, root, CocoImports.class);
        addAll(descriptors, root, CocoGlobalFieldsAndMethods.class);
        addAll(descriptors, root, CocoCharacters.class);
        addAll(descriptors, root, CocoTokens.class);
        addAll(descriptors, root, CocoTokenDecl.class);
        addAll(descriptors, root, CocoPragmas.class);
        addAll(descriptors, root, CocoPragmaDecl.class);
        addAll(descriptors, root, CocoComments.class);
        addAll(descriptors, root, CocoArbitraryStatements.class);
        addAll(descriptors, root, CocoArbitraryText.class);
        addAll(descriptors, root, CocoProduction.class);
        addAll(descriptors, root, CocoEnd.class);
        addAll(descriptors, root, CocoParserSpecification.class);
        addAll(descriptors, root, PsiComment.class);

        return descriptors.toArray(new FoldingDescriptor[descriptors.size()]);
    }

    private <T extends PsiElement> void addAll(@NotNull List<FoldingDescriptor> foldingDescriptors, @NotNull PsiElement root, @NotNull Class<T> elementClass) {
        FoldingGroup group = FoldingGroup.newGroup(foldingGroups.get(elementClass));
        Collection<T> cocoBlocks = PsiTreeUtil.findChildrenOfType(root, elementClass);
        for (final T cocoBlock : cocoBlocks) {
            if (StringUtils.isNotBlank(cocoBlock.getText()))  {
                foldingDescriptors.add(new FoldingDescriptor(cocoBlock.getNode(), cocoBlock.getTextRange()));
            }
        }
    }

    @Nullable
    @Override
    public String getPlaceholderText(@NotNull ASTNode node) {
        PsiElement psiElement = node.getPsi();
        if (psiElement == null) {
            return null;
        }

        String completeText[] = psiElement.getText().trim().split("\n");

        if (psiElement instanceof CocoImports) {
            return "JAVA IMPORTS ...";
        }

        if (psiElement instanceof CocoGlobalFieldsAndMethods) {
            return "JAVA FIELDS AND METHODS ...";
        }

        if (psiElement instanceof CocoComments) {
            int size = ((CocoComments) psiElement).getCommentDeclList().size();
            return "COMMENTS ("+size+") ...";
        }

        if (psiElement instanceof CocoCharacters) {
            int size = ((CocoCharacters) psiElement).getSetDeclList().size();
            return "CHARACTERS ("+size+") ...";
        }

        if (psiElement instanceof CocoTokens) {
            int size = ((CocoTokens) psiElement).getTokenDeclList().size();
            return "TOKENS ("+size+") ...";
        }

        if (psiElement instanceof CocoPragmas) {
            int size = ((CocoPragmas) psiElement).getPragmaDeclList().size();
            return "PRAGMAS ("+size+") ...";
        }

        if (psiElement instanceof CocoParserSpecification) {
            int size = ((CocoParserSpecification) psiElement).getProductionList().size();
            return "PRODUCTIONS ("+size+") ...";
        }

        if (psiElement instanceof PsiComment) {
            if (psiElement.getText().startsWith("//")) {
                return "// ...";
            }

            return "/* ... */";
        }

        if (psiElement instanceof CocoEnd) {
            PsiElement ident = ((CocoEnd) psiElement).getNameIdentifier();
            if (ident != null) {
                return "END " + ident.getText() + ".";
            }

            return "END ??.";
        }

        if (psiElement instanceof CocoProduction) {
            String name = ((CocoProduction) psiElement).getName();
            return name + " ...";
        }

        if(completeText.length == 1) {
            return completeText[0] + "...";
        }

        return "...";
    }

    @Override
    public boolean isCollapsedByDefault(@NotNull ASTNode node) {
        return node.getText().contains("\n");
    }
}