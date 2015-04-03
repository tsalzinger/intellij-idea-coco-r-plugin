package Coco;

/**
 * Created by Thomas on 03/04/2015.
 */
public class IntellijErrors extends Errors {
    private int errorCount = 0;
    private int warningCount = 0;

    final StringBuilder syntactialErrors = new StringBuilder();
    final StringBuilder semanticErrorsWithLineInfo = new StringBuilder();
    final StringBuilder semanticErrors = new StringBuilder();
    final StringBuilder warningsWithLineInfo = new StringBuilder();
    final StringBuilder warnings = new StringBuilder();

    public String getErrorMessage() {
        StringBuilder errorMessage = new StringBuilder();
        errorMessage.append(errorCount);
        errorMessage.append(" errors occured!\n");
        errorMessage.append("\n");
        if (syntactialErrors.length() > 0) {
            errorMessage.append("Syntax errors\n");
            errorMessage.append(syntactialErrors);
        }
        if (semanticErrors.length() > 0 || semanticErrorsWithLineInfo.length() > 0) {
            errorMessage.append("Semantic errors\n");
            errorMessage.append(semanticErrors);
            errorMessage.append(semanticErrorsWithLineInfo);
        }

        return errorMessage.toString();
    }

    public String getWarningMessage() {
        return String.valueOf(warningCount) + " warnings!\n\n" + warnings + warningsWithLineInfo;
    }

    @Override
    public void SynErr(int line, int column, int error) {
        errorCount++;
        syntactialErrors.append(line);
        syntactialErrors.append(":");
        syntactialErrors.append(column);
        syntactialErrors.append(" - ");
        syntactialErrors.append(error);
        syntactialErrors.append("\n");
    }

    @Override
    public void SemErr(int line, int column, String error) {
        errorCount++;
        semanticErrorsWithLineInfo.append(line);
        semanticErrorsWithLineInfo.append(":");
        semanticErrorsWithLineInfo.append(column);
        semanticErrorsWithLineInfo.append(" - ");
        semanticErrorsWithLineInfo.append(error);
        semanticErrorsWithLineInfo.append("\n");
    }

    @Override
    public void SemErr(String error) {
        errorCount++;
        semanticErrors.append(error);
    }

    @Override
    public void Warning(int line, int column, String warning) {
        warningCount++;
        warningsWithLineInfo.append(line);
        warningsWithLineInfo.append(":");
        warningsWithLineInfo.append(column);
        warningsWithLineInfo.append(" - ");
        warningsWithLineInfo.append(warning);
        warningsWithLineInfo.append("\n");
    }

    @Override
    public void Warning(String warning) {
        warningCount++;
        warnings.append(warning);
        warnings.append("\n");
    }

    public int getErrorCount() {
        return errorCount;
    }

    public int getWarningCount() {
        return warningCount;
    }
}
