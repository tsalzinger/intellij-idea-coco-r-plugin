package at.jku.ssw.coco.intellij;

import at.jku.ssw.coco.intellij.psi.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.indexing.FileBasedIndex;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Thomas Scheinecker <a href="mailto:tscheinecker@gmail.com">tscheinecker@gmail.com</a>
 */
public class CocoUtil {

    @NotNull
    public static List<String> findCompilerNames(@Nullable Project project) {
        return findCompilers(project)
                .stream()
                .map(CocoCompiler::getName)
                .filter(it -> !Objects.isNull(it))
                .collect(Collectors.toList());
    }

    @Contract("_, null -> null")
    @Nullable
    public static CocoCompiler findCompiler(@NotNull PsiFile file, @Nullable String name) {
        return findByName(findCompilers(file), name);
    }

    @NotNull
    public static List<CocoCompiler> findCompilers(@NotNull PsiFile file) {
        return findCompilers(file.getProject())
                .stream()
                .filter(compiler -> file.equals(compiler.getContainingFile()))
                .collect(Collectors.toList());
    }

    public static List<CocoCompiler> findCompilers(@Nullable Project project, @NotNull String name) {
        return findCompilers(project)
                .stream()
                .filter(compiler -> name.equals(compiler.getName()))
                .collect(Collectors.toList());
    }

    @NotNull
    public static List<CocoCompiler> findCompilers(@Nullable Project project) {
        if (project == null) {
            return Collections.emptyList();
        }

        Collection<VirtualFile> virtualFiles = FileBasedIndex.getInstance().getContainingFiles(FileTypeIndex.NAME, CocoFileType.INSTANCE, GlobalSearchScope.allScope(project));
        List<CocoCompiler> compilers = new ArrayList<>();
        for (VirtualFile virtualFile : virtualFiles) {
            CocoFile cocoFile = (CocoFile) PsiManager.getInstance(project).findFile(virtualFile);
            if (cocoFile != null) {
                CocoCompiler[] cocoCompilers = PsiTreeUtil.getChildrenOfType(cocoFile, CocoCompiler.class);
                if (cocoCompilers != null) {
                    compilers.addAll(Arrays.asList(cocoCompilers));
                }
            }
        }
        return compilers;
    }

    @NotNull
    public static List<CocoSetDecl> findCharacterDeclarations(@NotNull PsiFile file) {
        CocoScannerSpecification scannerSpecification = PsiTreeUtil.getChildOfType(file, CocoScannerSpecification.class);

        if (scannerSpecification == null) {
            return Collections.emptyList();
        }

        CocoCharacters characters = scannerSpecification.getCharacters();

        if (characters == null) {
            return Collections.emptyList();
        }

        return characters.getSetDeclList();
    }

    @Contract("_, null -> null")
    @Nullable
    public static CocoSetDecl findCharacterDeclaration(@NotNull PsiFile file, @Nullable String name) {
        return findByName(findCharacterDeclarations(file), name);
    }

    @NotNull
    public static List<CocoProduction> findProductions(@NotNull PsiFile file) {
        CocoParserSpecification parserSpecification = PsiTreeUtil.getChildOfType(file, CocoParserSpecification.class);

        if (parserSpecification == null) {
            return Collections.emptyList();
        }

        return parserSpecification.getProductionList();
    }

    @Contract("_, null -> null")
    @Nullable
    public static CocoProduction findProduction(@NotNull PsiFile file, @Nullable String name) {
        return findByName(findProductions(file), name);
    }

    @NotNull
    public static List<CocoTokenDecl> findTokenDecls(@NotNull PsiFile file) {
        CocoScannerSpecification scannerSpecification = PsiTreeUtil.getChildOfType(file, CocoScannerSpecification.class);

        if (scannerSpecification == null) {
            return Collections.emptyList();
        }

        CocoTokens tokens = scannerSpecification.getTokens();
        if (tokens == null) {
            return Collections.emptyList();
        }

        return tokens.getTokenDeclList();
    }

    @Contract("_, null -> null")
    @Nullable
    public static CocoTokenDecl findTokenDecl(@NotNull PsiFile file, @Nullable String name) {
        return findByName(findTokenDecls(file), name);
    }

    @Contract("_, null -> null")
    @Nullable
    private static <T extends CocoNamedElement> T findByName(@NotNull Collection<T> collection, @Nullable String name) {
        if (StringUtils.isBlank(name)) {
            return null;
        }

        Optional<T> first = collection
                .stream()
                .filter(item -> name.equals(item.getName()))
                .findFirst();

        return first.isPresent() ? first.get() : null;
    }
}
