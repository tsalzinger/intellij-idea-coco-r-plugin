package at.jku.ssw.coco.intellij;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Map;

/**
 * @author Thomas Scheinecker <a href="mailto:tscheinecker@gmail.com">tscheinecker@gmail.com</a>
 */
public class CocoColorSettingsPage implements ColorSettingsPage {
    private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[]{
            new AttributesDescriptor("Block Comment", CocoSyntaxHighlighter.BLOCK_COMMENT),
            new AttributesDescriptor("Line Comment", CocoSyntaxHighlighter.LINE_COMMENT),
            new AttributesDescriptor("Keyword", CocoSyntaxHighlighter.KEYWORD),
            new AttributesDescriptor("Identifier", CocoSyntaxHighlighter.IDENTIFIER),
            new AttributesDescriptor("Semantic Actions", CocoSyntaxHighlighter.SEM_ACTION),
            new AttributesDescriptor("Markup Tags", CocoSyntaxHighlighter.MARKUP_TAG),
            new AttributesDescriptor("Char", CocoSyntaxHighlighter.CHAR),
            new AttributesDescriptor("String", CocoSyntaxHighlighter.STRING),
            new AttributesDescriptor("Braces", CocoSyntaxHighlighter.BRACES),
            new AttributesDescriptor("Brackets", CocoSyntaxHighlighter.BRACKETS),
            new AttributesDescriptor("Parentheses", CocoSyntaxHighlighter.PARENTHESES),
            new AttributesDescriptor("Operations ( + | - | .. | = )", CocoSyntaxHighlighter.OPERATION_SIGN),
            new AttributesDescriptor("Terminal Symbol", CocoSyntaxHighlighter.TERMINATOR)
    };

    @Nullable
    @Override
    public Icon getIcon() {
        return CocoIcons.FILE;
    }

    @NotNull
    @Override
    public SyntaxHighlighter getHighlighter() {
        return new CocoSyntaxHighlighter();
    }

    @NotNull
    @Override
    public String getDemoText() {
        try (BufferedReader reader = new BufferedReader(new FileReader("/at/jku/ssw/coco/intellij/colorSettingsDemo.ATG"))) {
            StringBuilder text = new StringBuilder();
            String input = reader.readLine();
            while (input != null) {
                text.append(input);
                input = reader.readLine();
            }
            return text.toString();
        } catch (java.io.IOException e) {
            return "// test\n" +
                    "\n" +
                    "/* ... */\n" +
                    "\n" +
                    "import\n" +
                    "    dddda.asdfd.asdf;\n" +
                    "import dddae.ass.*;\n" +
                    "import test.Easdl;\n" +
                    "import test.ad;\n" +
                    "\n" +
                    "COMPILER dfd2ks32l\n" +
                    "\n" +
                    "asfd\n" +
                    "\n" +
                    "IGNORECASE\n" +
                    "// SCANNER SPECIFICATION\n" +
                    "/*ddd*/\n" +
                    "CHARACTERS\n" +
                    "test = a + '\\u0AF0' .\n" +
                    "somethingelse = a - 'c'.\n" +
                    "another = 'a'..'z'.\n" +
                    "\n" +
                    "TOKENS\n" +
                    "  number = daasdf.\n" +
                    "  \"\\0\" =  'a' | de CONTEXT (\"b\\XXb\\\\b\\X\\vca\").\n" +
                    "\n" +
                    "PRAGMAS\n" +
                    "ident = b . (. a a some st\n" +
                    "uff .)\n" +
                    "jident2 = a .\n" +
                    "jetonemore = a . (. ad .)\n" +
                    "\n" +
                    "COMMENTS FROM \"a\" TO \"b\" NESTED\n" +
                    "COMMENTS FROM \"/**\" TO eol\n" +
                    "\n" +
                    "IGNORE 'a'..'z'\n" +
                    "\n" +
                    "// PARSER SPECIFIATION\n" +
                    "\n" +
                    "PRODUCTIONS\n" +
                    "ident < arbitrary text > (. arbitrary statements .) =\n" +
                    "    WEAK eof < act attributes > | IF (ANY) { no } |\n" +
                    "    IF (ANY) [\n" +
                    "        IF (ANY) {\n" +
                    "            IF (ANY) WEAK t\n" +
                    "        }\n" +
                    "    ] .\n" +
                    "END ident .";
        }
    }

    @Nullable
    @Override
    public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        return null;
    }

    @NotNull
    @Override
    public AttributesDescriptor[] getAttributeDescriptors() {
        return DESCRIPTORS;
    }

    @NotNull
    @Override
    public ColorDescriptor[] getColorDescriptors() {
        return new ColorDescriptor[0];
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return "Coco";
    }
}
