package at.jku.ssw.coco.intellij.psi.impl;

import at.jku.ssw.coco.intellij.psi.CocoCompiler;
import at.jku.ssw.coco.intellij.psi.CocoTypes;
import com.intellij.lang.ASTNode;

/**
 * @author Thomas Scheinecker <a href="mailto:tscheinecker@gmail.com">tscheinecker@gmail.com</a>
 */
public class CocoPsiImplUtil {
    public static String getCompilerName(CocoCompiler element) {
        ASTNode nameNode = element.getNode().findChildByType(CocoTypes.IDENT);
        return nameNode != null ? nameNode.getText() : null;
    }
}
