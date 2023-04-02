package org.imyuyu.idea.plugins.filesync.utils

import com.intellij.AppTopics
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.StoragePathMacros
import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileDocumentManagerListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectFileIndex
import org.imyuyu.idea.plugins.filesync.FileSyncPlugin
import org.sylfra.idea.plugins.remotesynchronizer.model.Config

/**
 *
 */
@State(name = "FileSync", storages = [Storage(StoragePathMacros.WORKSPACE_FILE)])
class ConfigStateComponent(private val project: Project) : PersistentStateComponent<Config> {
    private var config: Config

    init {
        config = defaultSettings
        addOnSaveListener()
    }

    private fun addOnSaveListener() {
        // Applies to all opened projects, which means that the same file could be synchronized multiple times across
        // different projects, which is fine
        val connection = ApplicationManager.getApplication().messageBus.connect()
        connection.subscribe(AppTopics.FILE_DOCUMENT_SYNC,
            object : FileDocumentManagerListener {
                override fun beforeDocumentSaving(document: Document) {
                    // Intercepting beforeDocumentSaving event is not accurate, should rely on an "after" event to be sure the
                    // file is saved before the sync. This should work most of times since the sync is launched as a separate
                    // thread, but should be improved
                    if (config.generalOptions.isCopyOnSave) {
                        val vFile = FileDocumentManager.getInstance().getFile(document)
                        if (vFile != null && ProjectFileIndex.getInstance(project).isInContent(vFile)) {
                            FileSyncPlugin.getInstance(project).launchSyncIfAllowed(arrayOf(vFile))
                        }
                    }
                }
            })
    }

    val defaultSettings: Config
        /**
         * Provided a settings bean with default values
         *
         * @return a settings bean with default values
         */
        get() = Config()

    /**
     * {@inheritDoc}
     */
    override fun getState(): Config {
        return config
    }

    /**
     * {@inheritDoc}
     */
    override fun loadState(`object`: Config) {
        config = `object`

        // Prevent both options to be true
        if (config.generalOptions.isSaveBeforeCopy) {
            config.generalOptions.isCopyOnSave = false
        }
    }
}
