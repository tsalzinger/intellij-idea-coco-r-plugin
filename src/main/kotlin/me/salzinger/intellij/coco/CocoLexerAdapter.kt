package me.salzinger.intellij.coco

import com.intellij.lexer.FlexAdapter

/**
 * @author Thomas Scheinecker [tscheinecker@gmail.com](mailto:tscheinecker@gmail.com)
 */
class CocoLexerAdapter : FlexAdapter(me.salzinger.intellij.coco.CocoLexer())
