package at.scheinecker.intellij.coco;

import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author Thomas Scheinecker <a href="mailto:tscheinecker@gmail.com">tscheinecker@gmail.com</a>
 */
public class CocoFileType extends LanguageFileType{
    public static final CocoFileType INSTANCE = new CocoFileType();

    private CocoFileType() {
        super(CocoLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "Cocol/R file";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "A Cocol/R compiler description file";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "ATG";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return CocoIcons.FILE;
    }
}
