package at.jku.ssw.coco.intellij;

import at.jku.ssw.coco.intellij.psi.CocoCompiler;
import at.jku.ssw.coco.intellij.psi.CocoEnd;
import at.jku.ssw.coco.intellij.psi.CocoSetDecl;
import at.jku.ssw.coco.intellij.psi.HasCocoCharacterReference;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public class CocoAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull final PsiElement element, @NotNull AnnotationHolder holder) {
        if (element instanceof CocoEnd) {
            CocoEnd cocoEnd = (CocoEnd) element;
            PsiElement ident = cocoEnd.getIdent();
            if (ident == null) {
                return;
            }
            String compilerName = ident.getText();
            CocoCompiler compiler = CocoUtil.findCompiler(element.getContainingFile(), compilerName);
            if (compiler != null) {
                Annotation annotation = holder.createInfoAnnotation(ident.getTextRange(), null);
                annotation.setTextAttributes(DefaultLanguageHighlighterColors.KEYWORD);
            } else {
                holder.createErrorAnnotation(ident.getTextRange(), "Unresolved COMPILER '" + compilerName + "'");
            }
        } else if (element instanceof HasCocoCharacterReference) {
            HasCocoCharacterReference cocoElement = (HasCocoCharacterReference) element;
            PsiElement ident = cocoElement.getIdent();
            if (ident != null) {
                String characterReferenceName = ((HasCocoCharacterReference) element).getCharacterReferenceName();
                CocoSetDecl characterDeclaration = CocoUtil.findCharacterDeclaration(element.getContainingFile(), characterReferenceName);

                if (characterDeclaration != null) {
                    Annotation annotation = holder.createInfoAnnotation(ident, null);
                    annotation.setTextAttributes(DefaultLanguageHighlighterColors.INSTANCE_FIELD);
                } else {
                    holder.createErrorAnnotation(ident.getTextRange(), "Unresolved Character '" + characterReferenceName + "'");
                }
            }
        }
    }
}