package at.scheinecker.intellij.coco

import at.scheinecker.intellij.coco.psi.CocoTypes
import com.intellij.lang.BracePair
import com.intellij.lang.PairedBraceMatcher
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IElementType

/**
 * Created by Thomas on 28/03/2015.
 */
class CocoBraceMatcher : PairedBraceMatcher {
    private val pairs = arrayOf(
            BracePair(CocoTypes.PAR_OPEN, CocoTypes.PAR_CLOSE, false),
            BracePair(CocoTypes.BRACK_OPEN, CocoTypes.BRACK_CLOSE, false),
            BracePair(CocoTypes.CURL_OPEN, CocoTypes.CURL_CLOSE, true),
            BracePair(CocoTypes.SMALLER_THEN, CocoTypes.GREATER_THEN, false),
            BracePair(CocoTypes.SEM_ACTION_START, CocoTypes.SEM_ACTION_END, true)
    )

    override fun getPairs(): Array<BracePair> {
        return pairs
    }

    override fun isPairedBracesAllowedBeforeType(lbraceType: IElementType, contextType: IElementType?): Boolean {
        return true
    }

    override fun getCodeConstructStart(file: PsiFile, openingBraceOffset: Int): Int {
        return openingBraceOffset
    }
}
