package at.jku.ssw.coco.intellij;

import at.jku.ssw.coco.intellij.psi.*;
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
                annotation.setTextAttributes(DefaultLanguageHighlighterColors.CLASS_REFERENCE);
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
                    int characterOffset = characterDeclaration.getTextRange().getStartOffset();
                    int referenceOffset = cocoElement.getTextRange().getStartOffset();

                    if (referenceOffset < characterOffset) {
                        holder.createWarningAnnotation(ident, "Character '" + characterReferenceName + "' used before its defined");
                    } else {
                        Annotation annotation = holder.createInfoAnnotation(ident, null);
                        annotation.setTextAttributes(DefaultLanguageHighlighterColors.INSTANCE_FIELD);
                    }
                } else {
                    holder.createErrorAnnotation(ident.getTextRange(), "Unresolved Character '" + characterReferenceName + "'");
                }
            }
        } else if (element instanceof HasCocoTokenOrProductionReference) {
            HasCocoTokenOrProductionReference cocoElement = (HasCocoTokenOrProductionReference) element;
            PsiElement ident = cocoElement.getIdent();
            if (ident != null) {
                String referenceName = cocoElement.getIdentText();
                CocoTokenDecl tokenDecl = CocoUtil.findTokenDecl(element.getContainingFile(), referenceName);
                CocoProduction production = CocoUtil.findProduction(element.getContainingFile(), referenceName);

                if (tokenDecl != null) {
                    int characterOffset = tokenDecl.getTextRange().getStartOffset();
                    int referenceOffset = cocoElement.getTextRange().getStartOffset();

                    if (referenceOffset < characterOffset) {
                        holder.createWarningAnnotation(ident, "Token '" + referenceName + "' used before its defined");
                    } else {
                        Annotation annotation = holder.createInfoAnnotation(ident, null);
                        annotation.setTextAttributes(DefaultLanguageHighlighterColors.INSTANCE_FIELD);
                    }
                } else if (production != null) {
                    Annotation annotation = holder.createInfoAnnotation(ident, null);
                    annotation.setTextAttributes(DefaultLanguageHighlighterColors.INSTANCE_FIELD);
                } else {
                    holder.createErrorAnnotation(ident.getTextRange(), "Unresolved Token or Production'" + referenceName + "'");
                }
            }
        } else if (element instanceof CocoCompiler) {
            PsiElement ident = ((CocoCompiler) element).getIdent();

            if (ident != null) {
                Annotation annotation = holder.createInfoAnnotation(ident, null);
                annotation.setTextAttributes(DefaultLanguageHighlighterColors.CLASS_NAME);
            }
        } else if (element instanceof CocoNamedElement) {
            PsiElement ident = ((CocoNamedElement) element).getIdent();

            if (ident != null) {
                // TODO check usage
                Annotation annotation = holder.createInfoAnnotation(ident, null);
                annotation.setTextAttributes(DefaultLanguageHighlighterColors.INSTANCE_FIELD);
            }
        }
    }
}