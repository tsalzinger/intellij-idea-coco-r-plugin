package at.scheinecker.intellij.coco.settings

import com.intellij.openapi.options.BaseConfigurable
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.util.ui.JBUI
import java.awt.Dimension
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel

class CocoSettingsConfigurable(private val project: Project) : BaseConfigurable(), Configurable.NoScroll {
    private var myPanel: JPanel? = null
    private var injectionLevelCombo: ComboBox<CocoInjectionMode>? = null

    override fun getDisplayName(): String {
        return "Coco/R"
    }

    private fun getSettings(): CocoConfiguration.State {
        return CocoConfiguration.getSettings(project)
    }

    private fun getSelectedInjectionMode(): CocoInjectionMode? {
        return injectionLevelCombo?.selectedItem as CocoInjectionMode?
    }

    override fun apply() {
        val injectionMode = getSelectedInjectionMode()                  
        val settings = CocoConfiguration.getSettings(project)
        if (injectionMode != null) {
            settings.injectionMode = injectionMode
        }

        CocoConfiguration.saveSettings(project, settings)
    }

    override fun createComponent(): JComponent? {
        val myPanel = object : JPanel(GridBagLayout()) {
            override fun getPreferredSize(): Dimension {
                return Dimension(-1, 400)
            }
        }

        val injectionSettings = JPanel(GridBagLayout())

        val injectionLevelCombo = ComboBox(CocoInjectionMode.values())
        injectionLevelCombo.selectedItem = CocoConfiguration.getSettings(project).injectionMode

        injectionLevelCombo.actionListeners

        val injectionLevelComboLabel = JLabel("Java Language Injection Mode")

        injectionSettings.add(injectionLevelComboLabel,
                GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, JBUI.insets(12, 6, 12, 0), 0, 0))
        injectionSettings.add(injectionLevelCombo,
                GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, JBUI.insets(6, 6, 12, 0), 0, 0))


        val gridBagConstraints = GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.NORTH, GridBagConstraints.BOTH, Insets(0, 0, 0, 0), 0, 0)
        myPanel.add(injectionSettings, gridBagConstraints)

        this.myPanel = injectionSettings
        this.injectionLevelCombo = injectionLevelCombo

        return injectionSettings

    }

    override fun reset() {
        injectionLevelCombo?.selectedItem = getSettings().injectionMode
    }

    override fun isModified(): Boolean {
        return getSettings().injectionMode != getSelectedInjectionMode()
    }

    override fun disposeUIResources() {
        myPanel = null
        injectionLevelCombo = null
    }
}