package at.jku.ssw.coco.intellij.psi.impl;

import at.jku.ssw.coco.intellij.psi.CocoNamedElement;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Thomas on 24/03/2015.
 */
public abstract class CocoNamedElementImpl extends ASTWrapperPsiElement implements CocoNamedElement {
    public CocoNamedElementImpl(@NotNull ASTNode node) {
        super(node);
    }
}
