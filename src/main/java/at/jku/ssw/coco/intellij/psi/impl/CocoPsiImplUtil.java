package at.jku.ssw.coco.intellij.psi.impl;

import at.jku.ssw.coco.intellij.CocoIcons;
import at.jku.ssw.coco.intellij.psi.CocoCompiler;
import at.jku.ssw.coco.intellij.psi.CocoElementFactory;
import at.jku.ssw.coco.intellij.psi.CocoTypes;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author Thomas Scheinecker <a href="mailto:tscheinecker@gmail.com">tscheinecker@gmail.com</a>
 */
public class CocoPsiImplUtil {


    public static String getName(CocoCompiler element) {
        ASTNode nameNode = element.getNode().findChildByType(CocoTypes.IDENT);
        return nameNode != null ? nameNode.getText() : null;
    }

    public static PsiElement setName(CocoCompiler element, String newName) {
        ASTNode nameNode = element.getNode().findChildByType(CocoTypes.IDENT);
        if (nameNode != null) {

            CocoCompiler compiler = CocoElementFactory.createCompiler(element.getProject(), newName);


            ASTNode newNameNode = compiler.getNode().findChildByType(CocoTypes.IDENT);
            assert newNameNode != null;
            element.getNode().replaceChild(nameNode, newNameNode);
        }
        return element;
    }

    public static PsiElement getNameIdentifier(CocoCompiler element) {
        ASTNode nameNode = element.getNode().findChildByType(CocoTypes.IDENT);
        if (nameNode != null) {
            return nameNode.getPsi();
        } else {
            return null;
        }
    }

    public static ItemPresentation getPresentation(final CocoCompiler element) {
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
