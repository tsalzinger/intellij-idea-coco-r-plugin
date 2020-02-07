package me.salzinger.intellij.coco

import com.intellij.lexer.FlexAdapter

/**
 * @author Thomas Salzinger [tsalzinger@gmail.com](mailto:tsalzinger@gmail.com)
 */
class CocoLexerAdapter : FlexAdapter(me.salzinger.intellij.coco.CocoLexer())
