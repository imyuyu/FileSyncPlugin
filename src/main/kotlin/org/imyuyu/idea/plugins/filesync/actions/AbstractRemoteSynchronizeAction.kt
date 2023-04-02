package org.imyuyu.idea.plugins.filesync.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.util.NlsActions
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import org.imyuyu.idea.plugins.filesync.FileSyncPlugin
import org.sylfra.idea.plugins.remotesynchronizer.utils.ConfigPathsManager
import org.sylfra.idea.plugins.remotesynchronizer.utils.Utils

/**
 * Common code for [RemoteSynchronizeOpenedAction] and
 * [RemoteSynchronizeSelectedAction]
 */
abstract class AbstractRemoteSynchronizeAction(
    text: @NlsActions.ActionText String?,
    description: @NlsActions.ActionDescription String?
) : AnAction(text, description, null) {
    /**
     * Copy selected/opened files
     */
    override fun actionPerformed(e: AnActionEvent) {
        val plugin = Utils.getPlugin(e)
        val files = getFiles(plugin, e.dataContext) ?: return
        if (plugin.config.generalOptions.isSaveBeforeCopy) FileDocumentManager.getInstance().saveAllDocuments()
        if (!plugin.copierThreadManager.hasRunningSynchro()) refreshVfsIfJavaSelected(files, plugin.pathManager)
        plugin.launchSyncIfAllowed(files)
    }

    private fun refreshVfsIfJavaSelected(
        files: Array<VirtualFile>,
        pathManager: ConfigPathsManager
    ) {
        ApplicationManager.getApplication().runWriteAction(Runnable {
            for (i in files.indices) if (pathManager.isJavaSource(files[i])) {
                LocalFileSystem.getInstance().refresh(false)
                return@Runnable
            }
        })
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabled = isEnabled(e)
    }

    protected open fun isEnabled(e: AnActionEvent?): Boolean {
        val plugin = Utils.getPlugin(e)
        return (plugin != null
                && (plugin.config.generalOptions.isAllowConcurrentRuns
                || !plugin.copierThreadManager.hasRunningSynchro()))
    }

    protected abstract fun getFiles(
        plugin: FileSyncPlugin,
        dataContext: DataContext?
    ): Array<VirtualFile>?
}
