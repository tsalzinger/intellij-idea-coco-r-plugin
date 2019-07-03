package me.salzinger.intellij.coco.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.LiteralTextEscaper
import com.intellij.psi.PsiLanguageInjectionHost

/**
 * Created by Thomas on 27/03/2015.
 */
abstract class CocoEmbeddedLanguageType(node: ASTNode) : ASTWrapperPsiElement(node), PsiLanguageInjectionHost {
    override fun updateText(p0: String): PsiLanguageInjectionHost {
        return this
    }

    override fun createLiteralTextEscaper(): LiteralTextEscaper<out PsiLanguageInjectionHost> {
        return LiteralTextEscaper.createSimple(this)
    }

    override fun isValidHost(): Boolean {
        return true
    }
}
