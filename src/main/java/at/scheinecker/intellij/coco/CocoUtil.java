package at.scheinecker.intellij.coco;

import at.scheinecker.intellij.coco.psi.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiNameIdentifierOwner;
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

    @NotNull
    public static List<CocoProduction> findProductions(@Nullable Project project) {
        List<CocoProduction> characterDecls = new ArrayList<>();
        List<PsiFile> allFiles = getAllFiles(project);
        for (PsiFile file : allFiles) {
            characterDecls.addAll(findProductions(file));
        }
        return characterDecls;
    }

    @NotNull
    public static List<CocoSetDecl> findCharacterDecls(@Nullable Project project) {
        List<CocoSetDecl> characterDecls = new ArrayList<>();
        List<PsiFile> allFiles = getAllFiles(project);
        for (PsiFile file : allFiles) {
            characterDecls.addAll(findCharacterDeclarations(file));
        }
        return characterDecls;
    }

    @NotNull
    public static List<CocoTokenDecl> findTokenDecls(@Nullable Project project) {
        List<CocoTokenDecl> tokenDecls = new ArrayList<>();
        List<PsiFile> allFiles = getAllFiles(project);
        for (PsiFile file : allFiles) {
            tokenDecls.addAll(findTokenDecls(file));
        }
        return tokenDecls;
    }

    @NotNull
    private static List<PsiFile> getAllFiles(@Nullable Project project) {
        if (project == null) {
            return Collections.emptyList();
        }

        Collection<VirtualFile> virtualFiles = FileBasedIndex.getInstance().getContainingFiles(FileTypeIndex.NAME, CocoFileType.INSTANCE, GlobalSearchScope.allScope(project));

        List<PsiFile> psiFiles = new ArrayList<>();
        PsiManager psiManager = PsiManager.getInstance(project);

        for (VirtualFile virtualFile : virtualFiles) {
            psiFiles.add(psiManager.findFile(virtualFile));
        }

        return psiFiles;
    }

    @NotNull
    public static List<CocoTokenDecl> findTokenDecls(@Nullable Project project, @Nullable String name) {
        return findAllByName(findTokenDecls(project), name);
    }

    @NotNull
    public static List<CocoSetDecl> findCharacterDecls(@Nullable Project project, @Nullable String name) {
        return findAllByName(findCharacterDecls(project), name);
    }

    @NotNull
    public static List<CocoProduction> findProductions(@Nullable Project project, @Nullable String name) {
        return findAllByName(findProductions(project), name);
    }

    @Contract("_, null -> null")
    @Nullable
    public static CocoTokenDecl findTokenDecl(@NotNull PsiFile file, @Nullable String name) {
        return findByName(findTokenDecls(file), name);
    }

    @Contract("_, null -> null")
    @Nullable
    private static <T extends CocoNamedElement> T findByName(@NotNull List<T> collection, @Nullable String name) {
        if (StringUtils.isBlank(name)) {
            return null;
        }

        Optional<T> first = findAllByName(collection, name)
                .stream()
                .findFirst();

        return first.isPresent() ? first.get() : null;
    }

    @NotNull
    private static <T extends PsiNameIdentifierOwner> List<T> findAllByName(@NotNull List<T> collection, @Nullable String name) {
        if (StringUtils.isBlank(name)) {
            return collection;
        }

        return collection
                .stream()
                .filter(item -> name.equals(item.getName()))
                .collect(Collectors.toList());
    }
}
