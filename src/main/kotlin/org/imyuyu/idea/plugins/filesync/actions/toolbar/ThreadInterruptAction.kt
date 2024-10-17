package org.imyuyu.idea.plugins.filesync.actions.toolbar

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import org.imyuyu.idea.plugins.filesync.utils.Utils.getCurrentCopierThread

/**
 * Interrupt current synchronization
 */
class ThreadInterruptAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val t = getCurrentCopierThread(e)
        if (!t!!.isInterrupted) t.interrupt()
    }

    override fun update(e: AnActionEvent) {
        val t = getCurrentCopierThread(e)
        val show = t == null || !t.isInterrupted
        val enabled = t != null && t.isActive
        e.presentation.isVisible = show
        e.presentation.isEnabled = enabled
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }
}
