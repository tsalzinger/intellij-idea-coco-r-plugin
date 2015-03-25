package at.jku.ssw.coco.intellij;

import at.jku.ssw.coco.intellij.psi.CocoCompiler;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteralExpression;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CocoAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull final PsiElement element, @NotNull AnnotationHolder holder) {
        if (element instanceof PsiLiteralExpression) {
            PsiLiteralExpression literalExpression = (PsiLiteralExpression) element;
            Object value = literalExpression.getValue();
            if (value != null && value instanceof String) {
                String text = (String) value;
                if (text.startsWith("coco:")) {
                    Project project = element.getProject();
                    String key = text.substring("coco:".length());
                    List<CocoCompiler> compilers = CocoUtil.findCompilers(project, key);
                    int startOffset = element.getTextRange().getStartOffset() + 6;
                    if (compilers.size() == 1) {
                        TextRange range = new TextRange(startOffset, startOffset + key.length());
                        Annotation annotation = holder.createInfoAnnotation(range, null);
                        annotation.setTextAttributes(DefaultLanguageHighlighterColors.KEYWORD);
                    } else if (compilers.size() == 0) {
                        TextRange range = new TextRange(startOffset, element.getTextRange().getEndOffset() - 1);
                        holder.createErrorAnnotation(range, "Unresolved COMPILER '" + key + "'");
                    }
                }
            }
        }
    }
}