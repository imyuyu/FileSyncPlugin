package org.imyuyu.idea.plugins.filesync.actions.toolbar

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import org.imyuyu.idea.plugins.filesync.utils.Utils.getCurrentCopierThread
import org.imyuyu.idea.plugins.filesync.utils.Utils.getPlugin

/**
 * Stop current synchronization
 */
class ThreadStopAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        getPlugin(e)!!.consolePane.currentConsole.thread.stop()
    }

    override fun update(e: AnActionEvent) {
        val t = getCurrentCopierThread(e)
        val running = t != null && !t.isAvailable
        e.presentation.isEnabled = running
    }
}
