package org.imyuyu.idea.plugins.filesync.actions.toolbar

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import org.imyuyu.idea.plugins.filesync.utils.Utils.getCurrentCopierThread

/**
 * Remove the current console
 */
class RerunLastSynchroAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val t = getCurrentCopierThread(e)
        if (t!!.isAvailable) t.rerun()
    }

    override fun update(e: AnActionEvent) {
        val t = getCurrentCopierThread(e)
        val enabled = t != null && t.isAvailable && t.selectedFiles != null
        e.presentation.isEnabled = enabled
    }
}
