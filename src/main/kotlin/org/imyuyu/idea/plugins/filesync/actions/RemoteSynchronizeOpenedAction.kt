package org.imyuyu.idea.plugins.filesync.actions

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.vfs.VirtualFile
import org.imyuyu.idea.plugins.filesync.adaptedMessage
import org.imyuyu.idea.plugins.filesync.FileSyncPlugin

/**
 * Synchronize opened files
 */
class RemoteSynchronizeOpenedAction :
    AbstractRemoteSynchronizeAction(adaptedMessage("ACTION_SYNC_ALL_OPEN"), adaptedMessage("ACTION_SYNC_ALL_OPEN_DESC")) {

    override fun getFiles(plugin: FileSyncPlugin, dataContext: DataContext?): Array<VirtualFile> {
        return FileEditorManager.getInstance(plugin.project).openFiles
    }
}
