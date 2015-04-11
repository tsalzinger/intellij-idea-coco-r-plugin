package at.scheinecker.intellij.coco;

import at.scheinecker.intellij.coco.psi.*;
import com.intellij.lang.cacheBuilder.DefaultWordsScanner;
import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.lexer.FlexAdapter;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Thomas on 28/03/2015.
 */
public class CocoFindUsagesProvider implements FindUsagesProvider {
    private static final DefaultWordsScanner WORDS_SCANNER =
            new DefaultWordsScanner(new FlexAdapter(new CocoLexer()),
                    TokenSet.create(CocoTypes.IDENT),
                    TokenSet.create(CocoTypes.LINE_COMMENT, CocoTypes.BLOCK_COMMENT),
                    TokenSet.EMPTY);

    @Nullable
    @Override
    public WordsScanner getWordsScanner() {
        return WORDS_SCANNER;
    }

    @Override
    public boolean canFindUsagesFor(@NotNull PsiElement psiElement) {
        return psiElement instanceof CocoNamedElement;
    }

    @Nullable
    @Override
    public String getHelpId(@NotNull PsiElement psiElement) {
        return null;
    }

    @NotNull
    @Override
    public String getType(@NotNull PsiElement element) {
        if (element instanceof CocoTokenDecl) {
            return "token";
        }
        if (element instanceof CocoProduction) {
            return "production";
        }
        if (element instanceof CocoSetDecl) {
            return "character";
        }
        if (element instanceof CocoPragmaDecl) {
            return "pragma";
        }
        if (element instanceof CocoCompiler) {
            return "compiler";
        }

        return "";
    }

    @NotNull
    @Override
    public String getDescriptiveName(@NotNull PsiElement element) {
        if (element instanceof CocoNamedElement) {
            String name = ((CocoNamedElement) element).getName();
            if (name != null) {
                return name;
            }
        }

        return "";
    }

    @NotNull
    @Override
    public String getNodeText(@NotNull PsiElement element, boolean useFullName) {
        return element.getText();
    }
}
