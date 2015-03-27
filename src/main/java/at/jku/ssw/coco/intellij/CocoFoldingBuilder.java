package at.jku.ssw.coco.intellij;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilderEx;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.FoldingGroup;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CocoFoldingBuilder extends FoldingBuilderEx {
    @NotNull
    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement root, @NotNull Document document, boolean quick) {
        FoldingGroup group = FoldingGroup.newGroup("coco.comments");

        List<FoldingDescriptor> descriptors = new ArrayList<>();
//        Collection<CocoComment> commentBlocks = PsiTreeUtil.findChildrenOfType(root, CocoComment.class);
        // TODO
//        for (final PsiLiteralExpression commentBlock : commentBlocks) {
//            Project project = commentBlock.getProject();
//            if (properties.size() == 1) {
//                descriptors.add(new FoldingDescriptor(commentBlock.getNode(),
//                        new TextRange(commentBlock.getTextRange().getStartOffset() + "COMMENTS".length(),
//                                commentBlock.getTextRange().getEndOffset()), group) {
//                    @Nullable
//                    @Override
//                    public String getPlaceholderText() {
//                        return properties.get(0).getValue();
//                    }
//                });
//            }
//        }
        return descriptors.toArray(new FoldingDescriptor[descriptors.size()]);
    }

    @Nullable
    @Override
    public String getPlaceholderText(@NotNull ASTNode node) {
        return "...";
    }

    @Override
    public boolean isCollapsedByDefault(@NotNull ASTNode node) {
        return true;
    }
}