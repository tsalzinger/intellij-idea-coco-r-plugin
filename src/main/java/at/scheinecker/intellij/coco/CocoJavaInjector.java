package at.scheinecker.intellij.coco;

import at.scheinecker.intellij.coco.psi.CocoArbitraryStatements;
import at.scheinecker.intellij.coco.psi.CocoGlobalFieldsAndMethods;
import at.scheinecker.intellij.coco.psi.CocoImports;
import com.intellij.lang.java.JavaLanguage;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.InjectedLanguagePlaces;
import com.intellij.psi.LanguageInjector;
import com.intellij.psi.PsiLanguageInjectionHost;
import org.jetbrains.annotations.NotNull;

public class CocoJavaInjector implements LanguageInjector {
    @Override
    public void getLanguagesToInject(@NotNull PsiLanguageInjectionHost psiLanguageInjectionHost, @NotNull InjectedLanguagePlaces injectedLanguagePlaces) {
        if (psiLanguageInjectionHost instanceof CocoImports) {
            injectedLanguagePlaces.addPlace(JavaLanguage.INSTANCE, new TextRange(0, psiLanguageInjectionHost.getTextLength()), "", "");
        } else if (psiLanguageInjectionHost instanceof CocoGlobalFieldsAndMethods) {
            injectedLanguagePlaces.addPlace(JavaLanguage.INSTANCE, new TextRange(0, psiLanguageInjectionHost.getTextLength()), "class Dummy {", "}");
        } else if (psiLanguageInjectionHost instanceof CocoArbitraryStatements) {
            injectedLanguagePlaces.addPlace(JavaLanguage.INSTANCE, new TextRange(0, psiLanguageInjectionHost.getTextLength()), "class Dummy { void dummy() {", "}}");
        }
    }
}
