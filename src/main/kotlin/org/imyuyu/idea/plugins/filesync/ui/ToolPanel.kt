package org.imyuyu.idea.plugins.filesync.ui

import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.ActionManager
import org.imyuyu.idea.plugins.filesync.FileSyncPlugin
import org.imyuyu.idea.plugins.filesync.model.Config
import org.imyuyu.idea.plugins.filesync.model.ConfigListener
import org.imyuyu.idea.plugins.filesync.utils.LabelsFactory
import java.awt.BorderLayout
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingConstants
import javax.swing.border.EmptyBorder

class ToolPanel(private val consolePane: ThreadConsolePane, config: Config) : JPanel(BorderLayout()), ConfigListener {
    private val emptyLabel: JLabel

    init {
        config.addConfigListener(this)
        emptyLabel = JLabel(LabelsFactory[LabelsFactory.LB_NO_ACTIVE_TARGET])
        emptyLabel.horizontalAlignment = SwingConstants.CENTER
        border = EmptyBorder(2, 2, 2, 2)
        val actionGroup = ActionManager.getInstance()
            .getAction(FileSyncPlugin.WINDOW_ACTIONS_NAME) as ActionGroup
        val toolbar = ActionManager.getInstance()
            .createActionToolbar(
                FileSyncPlugin.PLUGIN_NAME, actionGroup,
                false
            )
        toolbar.targetComponent = consolePane
        add(toolbar.component, BorderLayout.WEST)
        add(emptyLabel, BorderLayout.CENTER)
        configChanged(config)
    }

    override fun configChanged(config: Config?) {
        if (config!!.hasActiveTarget()) {
            if (emptyLabel == getComponent(1)) {
                remove(1)
                add(consolePane, BorderLayout.CENTER)
                if (consolePane.componentCount > 0) consolePane.selectedIndex = 0
            }
        } else {
            if (consolePane == getComponent(1)) {
                remove(1)
                add(emptyLabel, BorderLayout.CENTER)
            }
        }
        repaint()
    }
}
