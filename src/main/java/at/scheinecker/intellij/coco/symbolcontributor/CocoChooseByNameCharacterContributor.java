package at.scheinecker.intellij.coco.symbolcontributor;

import at.scheinecker.intellij.coco.CocoUtil;
import at.scheinecker.intellij.coco.psi.CocoSetDecl;
import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by Thomas on 29/03/2015.
 */
public class CocoChooseByNameCharacterContributor implements ChooseByNameContributor {
    @NotNull
    @Override
    public String[] getNames(Project project, boolean includeNonProjectItems) {
        return CocoUtil.findCharacterDecls(project)
                .parallelStream()
                .map(CocoSetDecl::getName)
                .toArray(String[]::new);
    }

    @NotNull
    @Override
    public NavigationItem[] getItemsByName(String name, String pattern, Project project, boolean includeNonProjectItems) {
        List<CocoSetDecl> tokenDecls = CocoUtil.findCharacterDecls(project, name);
        return tokenDecls.toArray(new NavigationItem[tokenDecls.size()]);
    }
}
