package at.scheinecker.intellij.coco

import at.scheinecker.intellij.coco.psi.CocoProduction
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.icons.AllIcons
import com.intellij.psi.PsiElement

class CocoLineMarkerProvider : RelatedItemLineMarkerProvider() {
    override fun collectNavigationMarkers(element: PsiElement, result: MutableCollection<in RelatedItemLineMarkerInfo<*>>) {
        if (element is CocoProduction) {
            addMainProductionMarker(element, result)

            addImplementedByMarker(element, result)
        }
    }

    private fun addImplementedByMarker(element: CocoProduction, result: MutableCollection<in RelatedItemLineMarkerInfo<*>>) {
        val findMethod = CocoUtil.getParserClass(element.containingFile)?.findMethodsByName(element.name, false)?.firstOrNull()

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