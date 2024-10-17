package org.imyuyu.idea.plugins.filesync.synchronizing

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.ToolWindowManager
import org.imyuyu.idea.plugins.filesync.FileSyncPlugin
import org.imyuyu.idea.plugins.filesync.model.SyncronizingStatsInfo
import org.imyuyu.idea.plugins.filesync.model.TargetMappings
import org.imyuyu.idea.plugins.filesync.utils.LabelsFactory
import org.imyuyu.idea.plugins.filesync.ui.ThreadConsole
import org.imyuyu.idea.plugins.filesync.utils.PathsUtils
import org.imyuyu.idea.plugins.filesync.utils.Utils
import java.io.File
import java.io.IOException
import java.util.*

/**
 * Manage a synchronizing in background.
 *
 *
 * Method [.start]
 * must be invoked to start synchronizing of given files/dirs.
 *
 *
 * Virtual files are filtered to drop doublons and converted to absolute paths.
 * Then the remote dirs are inspected in order to delete obsolete files.
 * Finally, added and modified filkes are copied.
 *
 *
 * This thread may be interrupted, resumed or stopped
 */
class SynchronizerThread(
  @JvmField val plugin: FileSyncPlugin,
  @JvmField val targetMappings: TargetMappings
) {
    @JvmField
    var console: ThreadConsole? = null

    /**
     * Returns files last synchronized
     */
    var selectedFiles: Array<VirtualFile> = emptyArray()
        private set
    private val filesToCopy: Stack<String?>
    private val filesToDelete: Stack<File>
    private var state: Int
    private var statsInfo: SyncronizingStatsInfo? = null
    private var listener: SynchronizerThreadListener? = null

    init {
        filesToCopy = Stack()
        filesToDelete = Stack()
        state = STATE_STOPPED
    }

    fun setListener(listener: SynchronizerThreadListener?) {
        this.listener = listener
    }

    fun start(files: Array<VirtualFile>) {
        selectedFiles = files
        state = STATE_ACTIVE
        statsInfo = SyncronizingStatsInfo()
        val t = runThread()
        listener!!.threadStarted(this, t, files)
    }

    fun rerun() {
        start(selectedFiles)
    }

    /**
     * Stop current synchronizing and clear non copied files
     */
    fun stop() {
        state = STATE_STOPPED
        filesToCopy.clear()
        listener!!.threadStopped(this, statsInfo)
    }

    /**
     * Interrupt synchronizing
     */
    fun interrupt() {
        state = STATE_INTERRUPTED
        listener!!.threadInterrupted(this)
    }

    /**
     * Resume synchronizing
     */
    fun resume() {
        state = STATE_ACTIVE
        listener!!.threadResumed(this)
        runThread()
    }

    val isAvailable: Boolean
        /**
         * Is the thread stopped ?
         */
        get() = state == STATE_STOPPED
    val isActive: Boolean
        /**
         * Is the thread running ?
         */
        get() = state == STATE_ACTIVE
    val isInterrupted: Boolean
        /**
         * Is the thread interrupted ?
         */
        get() = state == STATE_INTERRUPTED

    /**
     * Starts thread
     */
    private fun runThread(): Thread {
        val t: Thread = object : Thread("FileSync") {
            override fun run() {
                ApplicationManager.getApplication().runReadAction { synchronize() }
            }
        }
        t.start()
        return t
    }

    /**
     * Delete obsolete files/dir and then starts copies
     */
    private fun synchronize() {
        filterFilesToCopy()
        filterFilesToDelete()
        while (state == STATE_ACTIVE && !filesToDelete.empty()) deleteFile(filesToDelete.pop(), statsInfo)
        while (state == STATE_ACTIVE && !filesToCopy.empty()) copyFile(filesToCopy.pop().toString(), statsInfo)
        if (state == STATE_ACTIVE) finished()
    }

    /**
     * Build stack of files to copy
     */
    private fun filterFilesToCopy() {
        filesToCopy.clear()
        addPathsToCopy(selectedFiles)
    }

    /**
     * Selected files are filtered to drop doublons and converted to absolute
     * paths. Add files recursively.
     */
    private fun addPathsToCopy(files: Array<VirtualFile>) {
        val pathManager = plugin.pathManager
        for (f in files) {
            if (f.isDirectory) {
                addPathsToCopy(f.children)
            } else {
                if (!filesToCopy.contains(f.path)) {
                    filesToCopy.push(f.path)
                }
                if (pathManager.isJavaSource(f)) {
                    val classFilePaths = plugin.javaSupport!!.getClassFilePaths(f)
                    if (classFilePaths != null) {
                        for (path in classFilePaths) {
                            if (!filesToCopy.contains(path)) {
                                filesToCopy.push(path)
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Build stack of files to delete
     * Inspects all files under destinations within included paths. Keep only
     * files which are contained in files selection
     */
    private fun filterFilesToDelete() {
        filesToDelete.clear()

        // If there is no directory selected, no need to look for files to delete
        var dirSelected = false
        for (selectedFile in selectedFiles) {
            if (selectedFile.isDirectory) {
                dirSelected = true
                break
            }
        }
        if (!dirSelected) return
        val includedMappings = targetMappings.synchroMappings
        for (p in includedMappings) {
            if (p!!.isDeleteObsoleteFiles) {
                val destPath = plugin.pathManager.expandPath(p.destPath, false)
                pushFilesToDelete(File(destPath))
            }
        }
    }

    /**
     * Add recursively files to delete
     */
    private fun pushFilesToDelete(destFile: File) {
        if (!destFile.exists()) return
        val srcPath = plugin.pathManager
            .getSrcPath(targetMappings, PathsUtils.toModelPath(destFile))
        if (srcPath != null) {
            val srcFile = File(srcPath)
            if ((!srcFile.exists()
                        || plugin.pathManager.isExcludedFromCopy(targetMappings, srcPath))
                && isContainedInSelection(srcPath)
                && !filesToDelete.contains(destFile)
            ) {
                filesToDelete.push(destFile)
            }
        }
        if (destFile.isDirectory) {
            val children = destFile.listFiles()
            if (children != null) for (aChildren in children) {
                pushFilesToDelete(aChildren)
            }
        }
    }

    /**
     * Does this path belong to file selection ?
     */
    private fun isContainedInSelection(path: String): Boolean {
        for (file in selectedFiles) {
            if (plugin.pathManager.isRelativePath(file.path, path)) {
                return true
            }
        }
        return false
    }

    /**
     * Delete a file and update stats
     */
    private fun deleteFile(f: File, statsInfo: SyncronizingStatsInfo?) {
        val isFile = f.isFile
        if (!isFile) {
            val children = f.listFiles()
            if (children == null || children.size > 0) return
        }
        listener!!.fileDeleting(this, f.absolutePath)
        val deleted = plugin.config.generalOptions.isSimulationMode || f.delete()
        if (deleted) {
            if (isFile) statsInfo!!.addDeleted()
        } else {
            statsInfo!!.addFailure()
            if (isFile) listener!!.fileDeletionFailed(this) else listener!!.dirDeletionFailed(this, f.absolutePath)
        }
    }

    /**
     * Try to copy specified file and update stats info
     */
    private fun copyFile(srcPath: String, statsInfo: SyncronizingStatsInfo?) {
        val pathsManager = plugin.pathManager
        val destPath = pathsManager.getRemotePath(targetMappings, srcPath)

        // Destination path not found
        if (destPath == null) {
            statsInfo!!.addExcluded()
            listener!!.fileCopying(this, srcPath, null, TYPE_COPY_EXCLUDED)
        } else {
            val srcFile = File(srcPath)
            val destFile = File(destPath)
            val copyType: Int
            copyType =
                if (!srcFile.exists()) TYPE_COPY_NOCLASS else if (!destFile.exists()) TYPE_COPY_NEW else if (srcFile.lastModified() == destFile.lastModified()) TYPE_COPY_IDENTICAL else if (destFile.isFile) TYPE_COPY_REPLACE else -1

            // File ignored
            if (copyType == TYPE_COPY_IDENTICAL || copyType == TYPE_COPY_NOCLASS) {
                statsInfo!!.addIgnored()
                listener!!.fileCopying(this, srcPath, destPath, copyType)
            } else {
                listener!!.fileCopying(this, srcPath, destPath, copyType)
                if (!checkParentFile(destFile)) {
                    listener!!.copyFailed(
                        this,
                        Throwable(LabelsFactory[LabelsFactory.MSG_CANT_MAKE_DIRS])
                    )
                    statsInfo!!.addFailure()
                    return
                } else {
                    // Copy files
                    try {
                        if (!plugin.config.generalOptions.isSimulationMode) Utils.copyFile(srcFile, destFile)
                        statsInfo!!.addSuccess()
                    } catch (ex: IOException) {
                        listener!!.copyFailed(this, ex)
                        statsInfo!!.addFailure()
                    }
                }
            }
        }
    }

    // Create directory if needed and allowed
    private fun checkParentFile(destFile: File): Boolean {
        val parentFile = destFile.parentFile
        val parentIsDir: Boolean
        parentIsDir = if (parentFile.isFile) {
            false
        } else {
            if (!plugin.config.generalOptions.isSimulationMode
                && !parentFile.exists()
                && plugin.config.generalOptions.isCreateMissingDirs
            ) {
                parentFile.mkdirs()
            } else true
        }
        return parentIsDir
    }

    /**
     * Called after the synchronizing has ended
     */
    protected fun finished() {
        state = STATE_STOPPED
        listener!!.threadFinished(this, statsInfo)
        if (statsInfo!!.hasFailures()) {
            val twManager = ToolWindowManager.getInstance(plugin.project)
            twManager.invokeLater { twManager.getToolWindow(FileSyncPlugin.PLUGIN_NAME)!!.show(null) }
        }
    }

    companion object {
        private const val STATE_ACTIVE = 0
        private const val STATE_INTERRUPTED = 1
        private const val STATE_STOPPED = 2
        const val TYPE_COPY_NEW = 0
        const val TYPE_COPY_REPLACE = 1
        const val TYPE_COPY_IDENTICAL = 2
        const val TYPE_COPY_NOCLASS = 3
        const val TYPE_COPY_EXCLUDED = 4
        const val TYPE_COPY_DELETED = 5
    }
}
