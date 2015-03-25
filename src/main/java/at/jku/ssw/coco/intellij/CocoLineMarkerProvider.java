package at.jku.ssw.coco.intellij;

import at.jku.ssw.coco.intellij.psi.CocoCompiler;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteralExpression;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class CocoLineMarkerProvider extends RelatedItemLineMarkerProvider {
    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element, Collection<? super RelatedItemLineMarkerInfo> result) {
        if (element instanceof PsiLiteralExpression) {
            PsiLiteralExpression literalExpression = (PsiLiteralExpression) element;
            Object value = literalExpression.getValue();
            if (value != null && value instanceof String) {
                String text = (String) value;
                if (text.startsWith("coco:")) {
                    String compilerName = text.substring("coco:".length());
                    Project project = element.getProject();
                    List<CocoCompiler> compilers = CocoUtil.findCompilers(project);
                    for (CocoCompiler compiler : compilers) {
                        if (Objects.equals(compiler.getName(), compilerName)) {
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
        }
    }
}