package me.salzinger.intellij.coco

import com.intellij.lang.BracePair
import com.intellij.lang.PairedBraceMatcher
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IElementType

/**
 * Created by Thomas on 28/03/2015.
 */
class CocoBraceMatcher : PairedBraceMatcher {
    private val pairs = arrayOf(
            BracePair(me.salzinger.intellij.coco.psi.CocoTypes.PAR_OPEN, me.salzinger.intellij.coco.psi.CocoTypes.PAR_CLOSE, false),
            BracePair(me.salzinger.intellij.coco.psi.CocoTypes.BRACK_OPEN, me.salzinger.intellij.coco.psi.CocoTypes.BRACK_CLOSE, false),
            BracePair(me.salzinger.intellij.coco.psi.CocoTypes.CURL_OPEN, me.salzinger.intellij.coco.psi.CocoTypes.CURL_CLOSE, false),
            BracePair(me.salzinger.intellij.coco.psi.CocoTypes.SMALLER_THEN, me.salzinger.intellij.coco.psi.CocoTypes.GREATER_THEN, false),
            BracePair(me.salzinger.intellij.coco.psi.CocoTypes.SEM_ACTION_START, me.salzinger.intellij.coco.psi.CocoTypes.SEM_ACTION_END, false),
            BracePair(me.salzinger.intellij.coco.psi.CocoTypes.ASSIGNMENT, me.salzinger.intellij.coco.psi.CocoTypes.TERMINATOR, false)
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
