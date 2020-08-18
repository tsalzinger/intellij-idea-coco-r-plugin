package me.salzinger.intellij.coco

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.psi.PsiElement

class CocoLineMarkerProvider : RelatedItemLineMarkerProvider() {
    override fun collectNavigationMarkers(
        element: PsiElement,
        result: MutableCollection<in RelatedItemLineMarkerInfo<*>>
    ) {
        if (element is me.salzinger.intellij.coco.psi.CocoProduction) {
            addMainProductionMarker(element, result)
        }
    }

    private fun addMainProductionMarker(
        element: me.salzinger.intellij.coco.psi.CocoProduction,
        result: MutableCollection<in RelatedItemLineMarkerInfo<*>>
    ) {
        val compiler = findCompilers(element.containingFile).findByName(element.name)
        if (compiler != null) {
            val builder = NavigationGutterIconBuilder
                .create(CocoIcons.FILE)
                .setTarget(compiler)
                .setTooltipText("Entry point production for grammar")
            result.add(builder.createLineMarkerInfo(element.ident))
        }
    }
}
