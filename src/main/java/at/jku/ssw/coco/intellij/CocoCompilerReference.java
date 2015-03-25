package at.jku.ssw.coco.intellij;

import at.jku.ssw.coco.intellij.psi.CocoCompiler;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CocoCompilerReference extends PsiPolyVariantReferenceBase<PsiElement> implements PsiPolyVariantReference {

    public CocoCompilerReference(@NotNull PsiElement element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        List<CocoCompiler> compilers = CocoUtil.findCompilers(myElement.getProject());
        List<LookupElement> variants = new ArrayList<>();

        for (CocoCompiler compiler : compilers) {
            variants.add(
                    LookupElementBuilder
                            .create(compiler)
                            .withIcon(CocoIcons.FILE)
                            .withTypeText(compiler.getContainingFile().getName())
            );
        }

        return variants.toArray();
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        List<CocoCompiler> compilers = CocoUtil.findCompilers(myElement.getProject());
        List<ResolveResult> results = new ArrayList<>();
        for (CocoCompiler compiler : compilers) {
            results.add(new PsiElementResolveResult(compiler));
        }
        return results.toArray(new ResolveResult[results.size()]);
    }
}