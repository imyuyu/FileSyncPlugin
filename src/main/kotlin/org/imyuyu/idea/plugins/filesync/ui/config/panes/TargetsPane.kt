package org.imyuyu.idea.plugins.filesync.ui.config.panes

import org.imyuyu.idea.plugins.filesync.model.Config
import org.imyuyu.idea.plugins.filesync.ui.config.ActionsHolder.*
import org.imyuyu.idea.plugins.filesync.ui.config.TargetTabbedPane
import org.imyuyu.idea.plugins.filesync.utils.ConfigPathsManager
import org.imyuyu.idea.plugins.filesync.utils.LabelsFactory
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import javax.swing.JPanel
import javax.swing.JSeparator
import javax.swing.JToolBar

class TargetsPane(pathsManager: ConfigPathsManager?) : JPanel(), IConfigPane {
    private val tabbedPane: TargetTabbedPane

    init {
        tabbedPane = TargetTabbedPane(pathsManager!!)
    }

    override val title: String
        get() = LabelsFactory[LabelsFactory.PANEL_TARGETS]

    override fun isModified(config: Config): Boolean {
        return tabbedPane.isModified(config)
    }

    override fun reset(config: Config) {
        tabbedPane.reset(config)
    }

    override fun apply(config: Config) {
        tabbedPane.apply(config)
    }

    override fun buildUI(pathsManager: ConfigPathsManager?) {
        layout = GridBagLayout()
        val c = GridBagConstraints()

        // Toolbar
        val toolBar = JToolBar()
        toolBar.isBorderPainted = false
        toolBar.isFloatable = false
        toolBar.isRollover = true
        toolBar.add(AddTabAction(tabbedPane))
        toolBar.add(RemoveTabAction(tabbedPane))
        toolBar.add(JSeparator())
        toolBar.add(MoveTabToLeftAction(tabbedPane))
        toolBar.add(MoveTabToRightAction(tabbedPane))
        c.gridx = 0
        c.gridy = 0
        c.insets = Insets(0, 5, 2, 10)
        c.anchor = GridBagConstraints.NORTHWEST
        add(toolBar, c)

        // Tabs
        c.gridy++
        c.insets = Insets(0, 5, 3, 10)
        c.weighty = 1.0
        c.weightx = 1.0
        c.fill = GridBagConstraints.BOTH
        add(tabbedPane, c)
    }
}