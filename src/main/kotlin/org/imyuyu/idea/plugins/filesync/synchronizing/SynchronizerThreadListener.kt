package org.imyuyu.idea.plugins.filesync.synchronizing

import com.intellij.openapi.vfs.VirtualFile
import org.imyuyu.idea.plugins.filesync.model.SyncronizingStatsInfo

/**
 * Used to handle synchronizing events
 */
interface SynchronizerThreadListener {
    fun threadStarted(thread: SynchronizerThread?, runnable: Runnable?, files: Array<VirtualFile>)
    fun threadFinished(
        copierThread: SynchronizerThread?,
        statsInfo: SyncronizingStatsInfo?
    )

    fun threadStopped(thread: SynchronizerThread?, statsInfo: SyncronizingStatsInfo?)
    fun threadInterrupted(thread: SynchronizerThread?)
    fun threadResumed(thread: SynchronizerThread?)
    fun fileCopying(
        thread: SynchronizerThread?, src: String?, dest: String?,
        copyType: Int
    )

    fun fileDeleting(thread: SynchronizerThread?, path: String?)
    fun copyFailed(thread: SynchronizerThread?, t: Throwable?)
    fun dirDeletionFailed(
        syncronizerThread: SynchronizerThread?,
        path: String?
    )

    fun fileDeletionFailed(syncronizerThread: SynchronizerThread?)
}
