package org.imyuyu.idea.plugins.filesync.actions.toolbar

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import org.imyuyu.idea.plugins.filesync.utils.Utils.getCurrentCopierThread

/**
 * Resume current synchronization
 */
class ThreadResumeAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val t = getCurrentCopierThread(e)
        if (t!!.isInterrupted) t.resume()
    }

    override fun update(e: AnActionEvent) {
        val t = getCurrentCopierThread(e)
        e.presentation.isVisible = t != null && t.isInterrupted
    }
}
