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
        customFileTemplate.text = """
            COMPILER ${'$'}NAME
            PRODUCTIONS
                ${'$'}NAME = .
            END ${'$'}NAME.
        """.trimIndent()


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
