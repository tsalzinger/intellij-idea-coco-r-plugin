package me.salzinger.intellij.coco.java

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.icons.AllIcons
import com.intellij.psi.PsiElement

class CocoJavaLineMarkerProvider : RelatedItemLineMarkerProvider() {
    override fun collectNavigationMarkers(element: PsiElement, result: MutableCollection<in RelatedItemLineMarkerInfo<*>>) {
        if (element is me.salzinger.intellij.coco.psi.CocoProduction) {
            addImplementedByMarker(element, result)
        }
    }

    private fun addImplementedByMarker(element: me.salzinger.intellij.coco.psi.CocoProduction, result: MutableCollection<in RelatedItemLineMarkerInfo<*>>) {
        val findMethod = CocoJavaUtil
                .getParserClass(element.containingFile)
                ?.findMethodsByName(element.name, false)
                ?.firstOrNull()

        if (findMethod != null) {
            result.add(
                    NavigationGutterIconBuilder
                            .create(AllIcons.Gutter.ImplementedMethod)
                            .setTarget(findMethod)
                            .setTooltipText("Go to generated Parser code")
                            .createLineMarkerInfo(element.ident)
            )
        }
    }
}