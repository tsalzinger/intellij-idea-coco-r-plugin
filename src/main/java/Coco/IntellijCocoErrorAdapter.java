package Coco;

import com.intellij.openapi.compiler.CompilerMessage;
import com.intellij.openapi.compiler.CompilerMessageCategory;
import me.salzinger.intellij.coco.action.CocoCompilerContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thomas on 03/04/2015.
 */
public class IntellijCocoErrorAdapter extends Errors {
    final List<CompilerMessage> errors = new ArrayList<>();
    final List<CompilerMessage> warnings = new ArrayList<>();

    private final CocoCompilerContext context;

    public IntellijCocoErrorAdapter(final CocoCompilerContext context) {

        this.context = context;
    }

    @Override
    public void SynErr(int line, int column, int errorCode) {
        String errorMessage;
        switch (errorCode) {
            case 0:
                errorMessage = "EOF expected";
                break;
            case 1:
                errorMessage = "ident expected";
                break;
            case 2:
                errorMessage = "number expected";
                break;
            case 3:
                errorMessage = "string expected";
                break;
            case 4:
                errorMessage = "badString expected";
                break;
            case 5:
                errorMessage = "char expected";
                break;
            case 6:
                errorMessage = "\"COMPILER\" expected";
                break;
            case 7:
                errorMessage = "\"IGNORECASE\" expected";
                break;
            case 8:
                errorMessage = "\"CHARACTERS\" expected";
                break;
            case 9:
                errorMessage = "\"TOKENS\" expected";
                break;
            case 10:
                errorMessage = "\"PRAGMAS\" expected";
                break;
            case 11:
                errorMessage = "\"COMMENTS\" expected";
                break;
            case 12:
                errorMessage = "\"FROM\" expected";
                break;
            case 13:
                errorMessage = "\"TO\" expected";
                break;
            case 14:
                errorMessage = "\"NESTED\" expected";
                break;
            case 15:
                errorMessage = "\"IGNORE\" expected";
                break;
            case 16:
                errorMessage = "\"PRODUCTIONS\" expected";
                break;
            case 17:
                errorMessage = "\"=\" expected";
                break;
            case 18:
                errorMessage = "\".\" expected";
                break;
            case 19:
                errorMessage = "\"END\" expected";
                break;
            case 20:
                errorMessage = "\"+\" expected";
                break;
            case 21:
                errorMessage = "\"-\" expected";
                break;
            case 22:
                errorMessage = "\"..\" expected";
                break;
            case 23:
                errorMessage = "\"ANY\" expected";
                break;
            case 24:
                errorMessage = "\"<\" expected";
                break;
            case 25:
                errorMessage = "\"^\" expected";
                break;
            case 26:
                errorMessage = "\"out\" expected";
                break;
            case 27:
                errorMessage = "\">\" expected";
                break;
            case 28:
                errorMessage = "\",\" expected";
                break;
            case 29:
                errorMessage = "\"<.\" expected";
                break;
            case 30:
                errorMessage = "\".>\" expected";
                break;
            case 31:
                errorMessage = "\"[\" expected";
                break;
            case 32:
                errorMessage = "\"]\" expected";
                break;
            case 33:
                errorMessage = "\"|\" expected";
                break;
            case 34:
                errorMessage = "\"WEAK\" expected";
                break;
            case 35:
                errorMessage = "\"(\" expected";
                break;
            case 36:
                errorMessage = "\")\" expected";
                break;
            case 37:
                errorMessage = "\"{\" expected";
                break;
            case 38:
                errorMessage = "\"}\" expected";
                break;
            case 39:
                errorMessage = "\"SYNC\" expected";
                break;
            case 40:
                errorMessage = "\"IF\" expected";
                break;
            case 41:
                errorMessage = "\"CONTEXT\" expected";
                break;
            case 42:
                errorMessage = "\"(.\" expected";
                break;
            case 43:
                errorMessage = "\".)\" expected";
                break;
            case 44:
                errorMessage = "??? expected";
                break;
            case 45:
                errorMessage = "this symbol not expected in Coco";
                break;
            case 46:
                errorMessage = "this symbol not expected in TokenDecl";
                break;
            case 47:
                errorMessage = "invalid TokenDecl";
                break;
            case 48:
                errorMessage = "invalid AttrDecl";
                break;
            case 49:
                errorMessage = "invalid AttrDecl";
                break;
            case 50:
                errorMessage = "invalid AttrDecl";
                break;
            case 51:
                errorMessage = "invalid AttrDecl";
                break;
            case 52:
                errorMessage = "invalid AttrDecl";
                break;
            case 53:
                errorMessage = "invalid SimSet";
                break;
            case 54:
                errorMessage = "invalid Sym";
                break;
            case 55:
                errorMessage = "invalid Term";
                break;
            case 56:
                errorMessage = "invalid Factor";
                break;
            case 57:
                errorMessage = "invalid Attribs";
                break;
            case 58:
                errorMessage = "invalid Attribs";
                break;
            case 59:
                errorMessage = "invalid Attribs";
                break;
            case 60:
                errorMessage = "invalid Attribs";
                break;
            case 61:
                errorMessage = "invalid Attribs";
                break;
            case 62:
                errorMessage = "invalid TokenFactor";
                break;
            case 63:
                errorMessage = "invalid Bracketed";
                break;
            default:
                errorMessage = "error " + errorCode;
        }

        context.addCompilerMessage(CompilerMessageCategory.ERROR, errorMessage, line, column);
    }

    @Override
    public void SemErr(int line, int column, String error) {
        context.addCompilerMessage(CompilerMessageCategory.ERROR, error, line, column);
    }

    @Override
    public void SemErr(String error) {
        context.addCompilerMessage(CompilerMessageCategory.ERROR, error);
    }

    @Override
    public void Warning(int line, int column, String warning) {
        context.addCompilerMessage(CompilerMessageCategory.WARNING, warning, line, column);
    }

    @Override
    public void Warning(String warning) {
        context.addCompilerMessage(CompilerMessageCategory.WARNING, warning);
    }

    public int getErrorCount() {
        return errors.size();
    }

    public int getWarningCount() {
        return warnings.size();
    }

    public List<CompilerMessage> getErrors() {
        return errors;
    }

    public List<CompilerMessage> getWarnings() {
        return warnings;
    }
}
