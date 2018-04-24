package at.scheinecker.intellij.coco

import at.scheinecker.intellij.coco.psi.CocoProduction
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.psi.PsiElement

class CocoLineMarkerProvider : RelatedItemLineMarkerProvider() {
    override fun collectNavigationMarkers(element: PsiElement, result: MutableCollection<in RelatedItemLineMarkerInfo<*>>) {
        if (element is CocoProduction) {
            addMainProductionMarker(element, result)
        }
    }

    private fun addMainProductionMarker(element: CocoProduction, result: MutableCollection<in RelatedItemLineMarkerInfo<*>>) {
        val compiler = CocoUtil.findCompiler(element.containingFile, element.name)
        if (compiler != null) {
            val builder = NavigationGutterIconBuilder
                    .create(CocoIcons.FILE)
                    .setTarget(compiler)
                    .setTooltipText("Entry point production for grammar")
            result.add(builder.createLineMarkerInfo(element.ident))

        }
    }


}