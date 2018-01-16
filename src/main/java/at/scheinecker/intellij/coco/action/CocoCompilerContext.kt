package at.scheinecker.intellij.coco.action

import at.scheinecker.intellij.coco.psi.CocoFile
import com.intellij.compiler.CompilerMessageImpl
import com.intellij.openapi.compiler.CompilerMessage
import com.intellij.openapi.compiler.CompilerMessageCategory
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import java.util.*

class CocoCompilerContext(cocoFile: CocoFile, val outputDir: VirtualFile) {
    val executionId: UUID
    val inputFile: VirtualFile
    val compilerMessages: MutableList<CompilerMessage> = mutableListOf()
    val project: Project?

    val warningsCount: Int
        get() = compilerMessages.count { it.category == CompilerMessageCategory.WARNING }

    val errorsCount: Int
        get() = compilerMessages.count { it.category == CompilerMessageCategory.ERROR }

    init {
        this.project = cocoFile.project
        this.executionId = UUID.randomUUID()
        this.inputFile = cocoFile.virtualFile
    }

    @JvmOverloads
    fun addCompilerMessage(category: CompilerMessageCategory, message: String, row: Int = -1, col: Int = -1) {
        if (project == null) {
            return
        }

        compilerMessages.add(CompilerMessageImpl(project, category, message.trim(), inputFile, row, col, null))
    }
}
