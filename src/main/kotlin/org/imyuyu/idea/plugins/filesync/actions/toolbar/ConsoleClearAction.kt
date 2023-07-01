package org.imyuyu.idea.plugins.filesync.actions.toolbar

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import org.imyuyu.idea.plugins.filesync.utils.Utils.getPlugin

/**
 * Clear the current console
 */
class ConsoleClearAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val currentConsole = getPlugin(e)!!.consolePane
            .currentConsole
        currentConsole?.clear()
    }

    override fun update(e: AnActionEvent) {
        val plugin = getPlugin(e)
        e.presentation.isEnabled = (plugin != null && plugin.consolePane.currentConsole != null
                && !plugin.consolePane.currentConsole.isCleared)
    }
}
