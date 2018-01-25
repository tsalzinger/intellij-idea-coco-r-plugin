package at.scheinecker.intellij.coco.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project
import com.intellij.util.xmlb.XmlSerializerUtil


@State(name = "CocoSettings", storages = [(Storage("coco.xml"))])
class CocoConfiguration(private val project: Project) : PersistentStateComponent<CocoConfiguration.State> {
    private val myState = CocoConfiguration.State()

    override fun getState(): State {
        return myState.copy()
    }

    override fun loadState(state: State) {
        XmlSerializerUtil.copyBean(state, myState)
    }

    data class State(var injectionMode: CocoInjectionMode = CocoInjectionMode.SIMPLE)

    companion object {
        fun getSettings(project: Project): CocoConfiguration.State {
            return ServiceManager.getService(project, CocoConfiguration::class.java).state
        }
        fun saveSettings(project: Project, state: CocoConfiguration.State) {
            ServiceManager.getService(project, CocoConfiguration::class.java).loadState(state)
        }
    }
}