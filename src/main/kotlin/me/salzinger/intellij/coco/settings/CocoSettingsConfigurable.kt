package me.salzinger.intellij.coco.settings

import com.intellij.openapi.options.BaseConfigurable
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.Panel
import com.intellij.ui.layout.panel
import com.intellij.util.ui.UIUtil
import com.intellij.xml.util.XmlStringUtil
import javax.swing.DefaultListCellRenderer
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
        val injectionLevelCombo = ComboBox(CocoInjectionMode.values())
        injectionLevelCombo.renderer = object : DefaultListCellRenderer() {
            override fun setText(text: String?) {
                super.setText(text?.toLowerCase())
            }
        }

        val noteComponent = JBLabel("", UIUtil.ComponentStyle.REGULAR)

        val notePanel = Panel()
        notePanel.add(noteComponent)

        val myPanel = panel {
            row {
                JLabel("Java Language Injection Mode:")()
                injectionLevelCombo()
            }
            row {
                notePanel()
            }
        }

        this.myPanel = myPanel
        this.injectionLevelCombo = injectionLevelCombo

        injectionLevelCombo.selectedItem = CocoConfiguration.getSettings(project).injectionMode
        injectionLevelCombo.addActionListener {
            if ("comboBoxChanged" == it.actionCommand) {
                when (getSelectedInjectionMode()!!) {
                    CocoInjectionMode.DISABLED -> {
                        noteComponent.text =
                            XmlStringUtil.wrapInHtml("Java language injection is disabled and no editor support will be provided.")
                    }
                    CocoInjectionMode.SIMPLE -> {
                        noteComponent.text =
                            XmlStringUtil.wrapInHtml("Simple mode will allow code completion of generated Tokens, but is otherwise limited to a per element injection. This means no completion of other Java injected areas will be possible.")
                    }
                    CocoInjectionMode.ADVANCED -> {
                        noteComponent.text =
                            XmlStringUtil.wrapInHtml("Advanced mode will allow code completion of linked injection areas. For example Semantic Actions within a Production will have access to the declared attributes (parameters).")
                    }
                }
            }
        }

        return myPanel
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
