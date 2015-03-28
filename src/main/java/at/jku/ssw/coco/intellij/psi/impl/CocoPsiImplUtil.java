package at.jku.ssw.coco.intellij.psi.impl;

import at.jku.ssw.coco.intellij.CocoIcons;
import at.jku.ssw.coco.intellij.psi.*;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import org.apache.commons.lang.NotImplementedException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author Thomas Scheinecker <a href="mailto:tscheinecker@gmail.com">tscheinecker@gmail.com</a>
 */
public class CocoPsiImplUtil {


    public static String getName(PsiNamedElement element) {
        ASTNode nameNode = element.getNode().findChildByType(CocoTypes.IDENT);
        return nameNode != null ? nameNode.getText() : null;
    }

    public static PsiElement setName(PsiNamedElement element, String newName) {
        ASTNode nameNode = element.getNode().findChildByType(CocoTypes.IDENT);
        if (nameNode != null) {

            if (element instanceof CocoCompiler) {
                CocoCompiler compiler = CocoElementFactory.createCompiler(element.getProject(), newName);
                ASTNode newNameNode = compiler.getNode().findChildByType(CocoTypes.IDENT);
                assert newNameNode != null;
                element.getNode().replaceChild(nameNode, newNameNode);
            } else {
                throw new NotImplementedException();
            }

        }
        return element;
    }

    public static PsiElement getNameIdentifier(PsiNamedElement element) {
        ASTNode nameNode = element.getNode().findChildByType(CocoTypes.IDENT);
        if (nameNode != null) {
            return nameNode.getPsi();
        } else {
            return null;
        }
    }

    public static ItemPresentation getPresentation(final PsiNamedElement element) {
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                return element.getName();
            }

            @Nullable
            @Override
            public String getLocationString() {
                return element.getContainingFile().getName();
            }

            @Nullable
            @Override
            public Icon getIcon(boolean unused) {
                return CocoIcons.FILE;
            }
        };
    }

    @Nullable
    public static String getCharacterReferenceName(@NotNull HasCocoCharacterReference element) {
        return getIdentText(element);
    }

    @Nullable
    public static String getTokenReferenceName(@NotNull HasCocoTokenReference element) {
        return getIdentText(element);
    }

    @Nullable
    public static String getProductionReferenceName(@NotNull HasCocoProductionReference element) {
        return getIdentText(element);
    }

    @Nullable
    public static String getIdentText(@NotNull HasIdent element) {
        PsiElement ident = element.getIdent();
        if (ident != null) {
            return ident.getText();
        }

        return null;
    }
}
