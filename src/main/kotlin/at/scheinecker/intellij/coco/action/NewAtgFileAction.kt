package at.scheinecker.intellij.coco.action

import at.scheinecker.intellij.coco.CocoIcons
import com.intellij.ide.IdeBundle
import com.intellij.ide.actions.CreateFileAction
import com.intellij.ide.fileTemplates.FileTemplateUtil
import com.intellij.ide.fileTemplates.impl.CustomFileTemplate
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiElement
import com.intellij.util.IncorrectOperationException

private val NEW_ATG_FILE_TEMPLATE = """
// Set the name of your grammar here (and at the end of this grammar):
COMPILER ${'$'}NAME

// Add auxiliary methods and declaration here.


// If you want your generated compiler case insensitive add the
// keyword IGNORECASE here.


CHARACTERS
// Add character set declarations here.
// Examples:
//   letter = 'A'..'Z' + 'a'..'z'.
//   digit = "0123456789".
//   cr = '\r'.
//   lf = '\n'.


TOKENS
// Add token declarations here.
// Example:
//   ident = letter {letter | digit}.
//   number = digit {digit}.


PRAGMAS
// Add pragma declarations here.
// Example:
//   switch = '${'$'}' { digit | letter }. (. Optional semantic action .)


// Add comments here.
// Example for a multi-line block comment:
//   COMMENTS FROM "/*" TO "*/" NESTED
// Example for a single line comment:
//   COMMENTS FROM "//" TO lf


// Set the ignored characters (whitespaces) here, the blank character is
// ignored by default.
// Example, add line breaks to the ignore set.
//   IGNORE cr + lf


PRODUCTIONS

// Add your productions here, one must have the same name as the grammar,
// it will be the start symbol (entry point of your compiler).
// Example:
//   ${'$'}NAME = "BEGIN" { Statement } "END".
//   Statement = ident "=" number { "+" number } .

${'$'}NAME=
.

// End of your compiler specification, make sure the name here matches
// the grammar name at the start of this grammar.
END ${'$'}NAME.
""".trimIndent()

class NewAtgFileAction : CreateFileAction("Cocol/R ATG File", "Create new Cocol/R ATG File", CocoIcons.FILE) {

    override fun invokeDialog(project: Project, psiDirectory: PsiDirectory): Array<PsiElement> {
        val validator = AtgEnsuringValidator(project, psiDirectory)
        val createdElements: Array<PsiElement>
        if (ApplicationManager.getApplication().isUnitTestMode) {
            try {
                createdElements = validator.create("test")
            } catch (e: Exception) {
                throw RuntimeException(e)
            }

            return createdElements
        } else {
            try {
                Messages.showInputDialog(project, IdeBundle.message("prompt.enter.new.file.name"), IdeBundle.message("title.new.file"), templatePresentation.icon, null, validator)
                createdElements = validator.createdElements

                return createdElements
            } catch (e: Exception) {
                throw IncorrectOperationException(e)
            }

        }
    }

    private fun ensureAtgFileEnding(fileName: String?): String? {
        return if (fileName != null && !fileName.toLowerCase().endsWith(".atg")) {
            "$fileName.ATG"
        } else fileName

    }

    @Throws(Exception::class)
    override fun create(fileName: String, psiDirectory: PsiDirectory): Array<PsiElement> {
        val compilerName = fileName.removeSuffix(".ATG").removeSuffix(".atg").capitalize()

        // TODO - replace with registered file template
        val customFileTemplate = CustomFileTemplate("Cocol/R", "ATG")
        customFileTemplate.text = NEW_ATG_FILE_TEMPLATE

        val properties = mutableMapOf<String?, Any>(Pair("NAME", compilerName))
        return arrayOf(FileTemplateUtil.createFromTemplate(customFileTemplate, fileName, properties, psiDirectory, null))
    }

    private inner class AtgEnsuringValidator constructor(project: Project, psiDirectory: PsiDirectory) : CreateFileAction.MyValidator(project, psiDirectory) {

        @Throws(Exception::class)
        override fun create(fileName: String): Array<PsiElement> {
            return super.create(ensureAtgFileEnding(fileName))
        }

        override fun checkInput(fileName: String): Boolean {
            return super.checkInput(ensureAtgFileEnding(fileName)!!)
        }

        override fun canClose(fileName: String): Boolean {
            return super.canClose(ensureAtgFileEnding(fileName)!!)
        }
    }
}
