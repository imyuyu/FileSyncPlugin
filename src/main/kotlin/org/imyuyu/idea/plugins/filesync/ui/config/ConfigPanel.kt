package org.imyuyu.idea.plugins.filesync.ui.config

import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPanel
import com.intellij.ui.components.JBTabbedPane
import org.imyuyu.idea.plugins.filesync.FileSyncPlugin
import org.imyuyu.idea.plugins.filesync.model.Config
import org.imyuyu.idea.plugins.filesync.utils.ConfigPathsManager
import org.imyuyu.idea.plugins.filesync.ui.config.panes.GeneralPane
import org.imyuyu.idea.plugins.filesync.ui.config.panes.IConfigPane
import org.imyuyu.idea.plugins.filesync.ui.config.panes.LogPane
import org.imyuyu.idea.plugins.filesync.ui.config.panes.TargetsPane
import java.awt.BorderLayout
import java.awt.Component
import java.util.*
import javax.swing.JButton
import javax.swing.JPanel

/**
 * Configuration panel
 */
class ConfigPanel(plugin: FileSyncPlugin) : JPanel() {
    val currentProject: Project
    private val configPanes: Array<IConfigPane>

    init {
        configPanes = arrayOf(
            TargetsPane(plugin.pathManager),
            GeneralPane(plugin.project, plugin.pathManager),
            LogPane()
        )
        buildUI(plugin.pathManager)
        currentProject = plugin.project
    }

    fun isModified(config: Config): Boolean {
        for (configPane in configPanes) {
            if (configPane.isModified(config)) {
                return true
            }
        }
        return false
    }

    fun reset(config: Config) {
        for (configPane in configPanes) {
            configPane.reset(config)
        }
    }

    fun apply(config: Config) {
        for (configPane in configPanes) {
            configPane.apply(config)
        }
    }

    private fun buildUI(pathsManager: ConfigPathsManager) {
        val pnHeader = createHeaderPanel()
        val mainPane = JBTabbedPane()
        for (configPane in configPanes) {
            configPane.buildUI(pathsManager)
            mainPane.addTab(configPane.title, configPane as Component)
        }
        layout = BorderLayout()
        add(pnHeader, BorderLayout.NORTH)
        add(mainPane, BorderLayout.CENTER)
    }

    private fun createHeaderPanel(): JPanel {
        val pnImportExport: JPanel = JBPanel<JBPanel<*>>()
        pnImportExport.add(JButton(ActionsHolder.ImportAction(this)))
        pnImportExport.add(JButton(ActionsHolder.ExportAction(this)))
        val panel = JPanel(BorderLayout())
        panel.add(pnImportExport, BorderLayout.CENTER)
        panel.add(
            JBLabel(
                "version " +
                        Objects.requireNonNull(PluginManagerCore.getPlugin(PluginId.getId(FileSyncPlugin.PLUGIN_NAME)))?.version
            ),
            BorderLayout.EAST
        )
        return panel
    }
}
