package me.salzinger.intellij.coco.psi

import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil

inline fun <reified T : PsiElement> PsiElement?.findChild(): T? {
    return PsiTreeUtil.getChildOfType(this, T::class.java)
}

inline fun <reified T : PsiElement> PsiElement?.findChildren(): List<T> {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, T::class.java)
}
