package at.jku.ssw.coco.intellij;

import at.jku.ssw.coco.intellij.psi.*;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.searches.ReferencesSearch;
import org.jetbrains.annotations.NotNull;

public class CocoAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull final PsiElement element, @NotNull AnnotationHolder holder) {
        if (element instanceof CocoEnd) {
            CocoEnd cocoEnd = (CocoEnd) element;
            PsiElement ident = cocoEnd.getNameIdentifier();
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
            PsiElement ident = cocoElement.getNameIdentifier();
            if (ident != null) {
                String characterReferenceName = ((HasCocoCharacterReference) element).getName();
                CocoSetDecl characterDeclaration = CocoUtil.findCharacterDeclaration(element.getContainingFile(), characterReferenceName);

                if (characterDeclaration != null) {
                    int characterOffset = characterDeclaration.getTextRange().getStartOffset();
                    int referenceOffset = cocoElement.getTextRange().getStartOffset();

                    if (referenceOffset < characterOffset) {
                        holder.createErrorAnnotation(ident, "Character '" + characterReferenceName + "' used before its defined");
                    } else {
                        Annotation annotation = holder.createInfoAnnotation(ident, null);
                        annotation.setTextAttributes(DefaultLanguageHighlighterColors.INSTANCE_FIELD);
                    }
                } else {
                    holder.createErrorAnnotation(ident.getTextRange(), "Unresolved Character '" + characterReferenceName + "'");
                }
            }
        } else if (element instanceof CocoFactor) {
            CocoFactor cocoElement = (CocoFactor) element;
            PsiElement ident = cocoElement.getNameIdentifier();
            if (ident != null) {
                String referenceName = cocoElement.getName();
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

                    if (production.getFormalAttributes() != null && cocoElement.getActualAttributes() == null) {
                        holder.createErrorAnnotation(ident.getTextRange(), "Production '" + referenceName + "' defines formal attributes");
                    } else if (production.getFormalAttributes() == null && cocoElement.getActualAttributes() != null) {
                        holder.createErrorAnnotation(cocoElement.getActualAttributes().getTextRange(), "Production '" + referenceName + "' doesn't define formal attributes");
                    }
                } else {
                    holder.createErrorAnnotation(ident.getTextRange(), "Unresolved Token or Production '" + referenceName + "'");
                }
            }
        } else if (element instanceof CocoCompiler) {
            PsiElement ident = ((CocoCompiler) element).getNameIdentifier();

            if (ident != null) {
                Annotation annotation = holder.createInfoAnnotation(ident, null);
                annotation.setTextAttributes(DefaultLanguageHighlighterColors.CLASS_NAME);
                CocoProduction production = CocoUtil.findProduction(element.getContainingFile(), ident.getText());

                if (production == null) {
                    holder.createErrorAnnotation(ident, "Missing production for '" + ident.getText() + "'");
                }
            }

        } else if (element instanceof PsiNameIdentifierOwner) {
            PsiElement ident = ((PsiNameIdentifierOwner) element).getNameIdentifier();

            if (ident != null) {
                PsiReference first = ReferencesSearch.search(element).findFirst();

                if (first == null && element instanceof CocoProduction) {
                    CocoCompiler compiler = CocoUtil.findCompiler(element.getContainingFile(), ((CocoProduction) element).getName());
                    if (compiler != null) {
                        Annotation annotation = holder.createInfoAnnotation(ident, "Main production for grammar");
                        annotation.setTextAttributes(DefaultLanguageHighlighterColors.INSTANCE_FIELD);
                        return;
                    }
                }

                if (first == null) {
                    String warningText;

                    if (element instanceof CocoSetDecl) {
                        warningText = "Character '" + ident.getText() + "' is never used";
                    } else if (element instanceof CocoTokenDecl) {
                        if (element.getParent() instanceof CocoPragmaDecl) {
                            warningText = "Pragma '" + ident.getText() + "' is never used";
                        } else {
                            warningText = "Token '" + ident.getText() + "' is never used";
                        }
                    } else if (element instanceof CocoProduction) {
                        warningText = "Production '" + ident.getText() + "' is never used";
                    } else {
                        warningText = "'" + ident.getText() + "' is never used";
                    }

                    Annotation annotation = holder.createWeakWarningAnnotation(ident, warningText);
                    annotation.setTextAttributes(DefaultLanguageHighlighterColors.LINE_COMMENT);
                } else {
                    Annotation annotation = holder.createInfoAnnotation(ident, null);
                    annotation.setTextAttributes(DefaultLanguageHighlighterColors.INSTANCE_FIELD);
                }
            }
        }
    }
}