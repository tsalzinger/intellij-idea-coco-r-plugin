package at.scheinecker.intellij.coco.reference;

import at.scheinecker.intellij.coco.CocoUtil;
import at.scheinecker.intellij.coco.psi.CocoSet;
import at.scheinecker.intellij.coco.psi.HasCocoCharacterReference;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CocoCharacterReference extends AbstractRenamableReference<HasCocoCharacterReference> implements PsiReference {

    public CocoCharacterReference(@NotNull HasCocoCharacterReference element, TextRange textRange) {
        super(element, textRange);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        return CocoUtil.findCharacterDeclaration(myElement.getContainingFile(), myElement.getName());
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return CocoUtil.findCharacterDeclarations(myElement.getContainingFile())
                .stream()
                .map(it -> {
                    CocoSet set = it.getSet();
                    LookupElementBuilder lookupElementBuilder = LookupElementBuilder.create(it)
                            .withTypeText("Character");

                    if (set != null) {
                        return lookupElementBuilder.withTailText(" = " +set.getText(), true);
                    }

                    return lookupElementBuilder;
                })
                .toArray();
    }
}