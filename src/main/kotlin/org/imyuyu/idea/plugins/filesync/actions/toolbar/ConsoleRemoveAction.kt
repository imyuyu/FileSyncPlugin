package org.imyuyu.idea.plugins.filesync.actions.toolbar

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import org.imyuyu.idea.plugins.filesync.utils.Utils.getPlugin

/**
 * Remove the current console
 */
class ConsoleRemoveAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val plugin = getPlugin(e)
        val console = plugin!!.consolePane.removeCurrentConsole()
        plugin.copierThreadManager.removeThread(console.thread)
    }

    override fun update(e: AnActionEvent) {
        val plugin = getPlugin(e)
        e.presentation.isEnabled =
            (plugin != null && plugin.consolePane.componentCount > 1 && plugin.consolePane.currentConsole != null
                    && plugin.consolePane.currentConsole.thread.isAvailable
                    && !plugin.consolePane.currentConsole.isMainConsole())
    }
}
