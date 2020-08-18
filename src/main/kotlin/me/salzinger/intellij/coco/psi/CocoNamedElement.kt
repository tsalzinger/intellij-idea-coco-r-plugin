package me.salzinger.intellij.coco.psi

import com.intellij.navigation.NavigationItem
import com.intellij.psi.PsiNameIdentifierOwner

interface CocoNamedElement : PsiNameIdentifierOwner, NavigationItem
