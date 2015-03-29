package at.jku.ssw.coco.intellij;

import at.jku.ssw.coco.intellij.psi.CocoCompiler;
import at.jku.ssw.coco.intellij.psi.CocoProduction;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class CocoLineMarkerProvider extends RelatedItemLineMarkerProvider {
    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element, Collection<? super RelatedItemLineMarkerInfo> result) {
        if (element instanceof CocoProduction) {
            CocoCompiler compiler = CocoUtil.findCompiler(element.getContainingFile(), ((CocoProduction) element).getName());
            if (compiler != null) {
                NavigationGutterIconBuilder<PsiElement> builder =
                        NavigationGutterIconBuilder
                                .create(CocoIcons.FILE)
                                .setTarget(compiler)
                                .setTooltipText("Navigate to the COMPILER definition");
                result.add(builder.createLineMarkerInfo(element));
            }
        }
    }
}