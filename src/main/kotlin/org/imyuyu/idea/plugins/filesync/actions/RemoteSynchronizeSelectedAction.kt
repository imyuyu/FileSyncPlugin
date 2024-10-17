package org.imyuyu.idea.plugins.filesync.actions

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.vfs.VirtualFile
import org.imyuyu.idea.plugins.filesync.adaptedMessage
import org.imyuyu.idea.plugins.filesync.FileSyncPlugin
import org.imyuyu.idea.plugins.filesync.utils.Utils

/**
 * Synchronize selected files
 */
class RemoteSynchronizeSelectedAction :
    AbstractRemoteSynchronizeAction(adaptedMessage("ACTION_SYNC_THIS"), adaptedMessage("ACTION_SYNC_THIS_DESC")) {

    override fun getFiles(plugin: FileSyncPlugin, dataContext: DataContext?): Array<VirtualFile> {
        return plugin.javaSupport!!.getSelectedFiles(dataContext)
    }

    public override fun isEnabled(e: AnActionEvent): Boolean {
        // Some files must be selected
        return (super.isEnabled(e)
                && (Utils.getPlugin(e)!!.javaSupport!!.insideModule(e.dataContext) || CommonDataKeys.VIRTUAL_FILE_ARRAY.getData(
            e.dataContext
        ) != null))
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }
}
