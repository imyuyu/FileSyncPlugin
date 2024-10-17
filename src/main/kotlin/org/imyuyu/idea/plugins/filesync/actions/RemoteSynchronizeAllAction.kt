package org.imyuyu.idea.plugins.filesync.actions

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.VirtualFile
import org.imyuyu.idea.plugins.filesync.adaptedMessage
import org.imyuyu.idea.plugins.filesync.FileSyncPlugin

/**
 * Synchronize all project files
 */
class RemoteSynchronizeAllAction : AbstractRemoteSynchronizeAction(adaptedMessage("ACTION_SYNC_ALL"), adaptedMessage("ACTION_SYNC_ALL_DESC")) {

    override fun getFiles(plugin: FileSyncPlugin, dataContext: DataContext?): Array<VirtualFile> {
        return ProjectRootManager.getInstance(plugin.project).contentRoots
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }
}
