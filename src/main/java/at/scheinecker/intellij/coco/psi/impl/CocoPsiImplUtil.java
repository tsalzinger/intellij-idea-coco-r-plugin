package at.scheinecker.intellij.coco.psi.impl;

import at.scheinecker.intellij.coco.CocoIcons;
import at.scheinecker.intellij.coco.psi.CocoCompiler;
import at.scheinecker.intellij.coco.psi.CocoTypes;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author Thomas Scheinecker <a href="mailto:tscheinecker@gmail.com">tscheinecker@gmail.com</a>
 */
public class CocoPsiImplUtil {


    public static String getName(PsiNameIdentifierOwner element) {
        PsiElement nameIdentifier = element.getNameIdentifier();
        return nameIdentifier != null ? nameIdentifier.getText() : null;
    }

    public static PsiElement setName(PsiNameIdentifierOwner element, String newName) {
        PsiElement nameIdentifier = element.getNameIdentifier();

        if (nameIdentifier instanceof LeafPsiElement) {
            ((LeafPsiElement) nameIdentifier).replaceWithText(newName);
        } else {
            throw new UnsupportedOperationException("Cannot rename element of type " + element.getClass().getSimpleName());
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

    public static int getTextOffset(CocoCompiler cocoCompiler) {
        return cocoCompiler.getStartOffsetInParent() + cocoCompiler.getNameIdentifier().getStartOffsetInParent();
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
}
