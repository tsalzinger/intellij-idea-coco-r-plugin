package at.scheinecker.intellij.coco;

import at.scheinecker.intellij.coco.psi.CocoTypes;
import com.intellij.lang.BracePair;
import com.intellij.lang.PairedBraceMatcher;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;

/**
 * Created by Thomas on 28/03/2015.
 */
public class CocoBraceMatcher implements PairedBraceMatcher {
    private final BracePair[] pairs = new BracePair[]{
            new BracePair(CocoTypes.PAR_OPEN, CocoTypes.PAR_CLOSE, false),
            new BracePair(CocoTypes.BRACK_OPEN, CocoTypes.BRACK_CLOSE, false),
            new BracePair(CocoTypes.CURL_OPEN, CocoTypes.CURL_CLOSE, true),
            new BracePair(CocoTypes.SMALLER_THEN, CocoTypes.GREATER_THEN, false),
            new BracePair(CocoTypes.SEM_ACTION_START, CocoTypes.SEM_ACTION_END, true)
    };

    @Override
    public BracePair[] getPairs() {
        return pairs;
    }

    @Override
    public boolean isPairedBracesAllowedBeforeType(IElementType lbraceType, IElementType contextType) {
        return true;
    }

    @Override
    public int getCodeConstructStart(PsiFile file, int openingBraceOffset) {
        return openingBraceOffset;
    }
}
