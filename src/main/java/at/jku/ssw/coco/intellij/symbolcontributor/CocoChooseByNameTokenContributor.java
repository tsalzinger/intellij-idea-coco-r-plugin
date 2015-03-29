package at.jku.ssw.coco.intellij.symbolcontributor;

import at.jku.ssw.coco.intellij.CocoUtil;
import at.jku.ssw.coco.intellij.psi.CocoTokenDecl;
import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by Thomas on 29/03/2015.
 */
public class CocoChooseByNameTokenContributor implements ChooseByNameContributor {
    @NotNull
    @Override
    public String[] getNames(Project project, boolean includeNonProjectItems) {
        return CocoUtil.findTokenDecls(project)
                .parallelStream()
                .map(CocoTokenDecl::getName)
                .toArray(String[]::new);
    }

    @NotNull
    @Override
    public NavigationItem[] getItemsByName(String name, String pattern, Project project, boolean includeNonProjectItems) {
        List<CocoTokenDecl> tokenDecls = CocoUtil.findTokenDecls(project, name);
        return tokenDecls.toArray(new NavigationItem[tokenDecls.size()]);
    }
}
