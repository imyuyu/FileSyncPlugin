package org.imyuyu.idea.plugins.filesync.setting

import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.NlsContexts.ConfigurableName
import org.imyuyu.idea.plugins.filesync.adaptedMessage
import org.jetbrains.annotations.NonNls
import org.imyuyu.idea.plugins.filesync.FileSyncPlugin
import org.sylfra.idea.plugins.remotesynchronizer.model.Config
import org.sylfra.idea.plugins.remotesynchronizer.ui.config.ConfigPanel
import org.imyuyu.idea.plugins.filesync.utils.ConfigStateComponent
import javax.swing.JComponent

/**
 * config
 *
 *
 * date: 2023/3/5 18:56
 *
 * @author imyuyu
 */
class FileSyncConfigurable(private val project: Project) : SearchableConfigurable {
    // Settings panel
    private var panel: ConfigPanel? = null

    override fun getId(): @NonNls String {
        return FileSyncPlugin.PLUGIN_NAME
    }

    override fun getDisplayName(): @ConfigurableName String? {
        return adaptedMessage(FileSyncPlugin.PLUGIN_NAME);
    }

    override fun getHelpTopic(): @NonNls String? {
        return null
    }

    override fun createComponent(): JComponent? {
        panel = ConfigPanel(project.getService(FileSyncPlugin::class.java))
        return panel
    }

    override fun isModified(): Boolean {
        if(panel == null){
            return false;
        }
        return panel!!.isModified(config)
    }

    @Throws(ConfigurationException::class)
    override fun apply() {
        val config = config
        panel!!.apply(config)
        config!!.fireConfigChanged()
    }

    override fun reset() {
        panel!!.reset(config)
    }

    override fun disposeUIResources() {
        panel = null
    }

    val config: Config?
        get() = stateComponent.state
    val stateComponent: ConfigStateComponent
        get() = project.getService(ConfigStateComponent::class.java)
}
