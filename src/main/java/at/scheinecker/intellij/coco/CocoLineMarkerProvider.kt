package at.scheinecker.intellij.coco

import at.scheinecker.intellij.coco.psi.CocoProduction
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.icons.AllIcons
import com.intellij.openapi.components.ServiceManager
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope

class CocoLineMarkerProvider : RelatedItemLineMarkerProvider() {
    override fun collectNavigationMarkers(element: PsiElement, result: MutableCollection<in RelatedItemLineMarkerInfo<*>>) {
        if (element is CocoProduction) {
            addMainProductionMarker(element, result)

            addImplementedByMarker(element, result)
        }
    }

    private fun addImplementedByMarker(element: CocoProduction, result: MutableCollection<in RelatedItemLineMarkerInfo<*>>) {
        val javaPsiFacade = ServiceManager.getService(element.containingFile.project, JavaPsiFacade::class.java)

        val parserClassName = "${CocoUtil.getTargetPackage(element.containingFile).map { "${it}." }.orElse("")}Parser"
        val findMethod = javaPsiFacade.findClass(
                parserClassName,
                GlobalSearchScope.allScope(javaPsiFacade.project)
        )?.findMethodsByName(element.name, false)?.firstOrNull()

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