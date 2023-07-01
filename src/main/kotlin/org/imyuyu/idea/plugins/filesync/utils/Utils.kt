package org.imyuyu.idea.plugins.filesync.utils

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import org.imyuyu.idea.plugins.filesync.FileSyncPlugin
import org.imyuyu.idea.plugins.filesync.FileSyncPlugin.Companion.getInstance
import org.imyuyu.idea.plugins.filesync.synchronizing.SynchronizerThread
import java.awt.Dimension
import java.io.*
import javax.swing.JComponent

/**
 * Misc utility methods
 */
object Utils {
    fun adjustComponentSizesToMax(
        c1: JComponent,
        c2: JComponent
    ): Dimension {
        return adjustComponentSizesToMax(arrayOf(c1, c2))
    }

    fun adjustComponentSizesToMax(l: Array<JComponent>): Dimension {
        var width = 0
        var height = 0
        for (aL in l) {
            width = Math.max(width, aL.preferredSize.width)
            height = Math.max(height, aL.preferredSize.height)
        }
        val d = Dimension(width, height)
        for (aL in l) {
            aL.preferredSize = d
        }
        return d
    }

    @Throws(IOException::class)
    fun copyFile(srcFile: File, destFile: File) {
        if (!srcFile.exists()) return
        val `in` = FileInputStream(srcFile)
        val out = FileOutputStream(destFile)
        val buffer = ByteArray(8192)
        var count = 0
        do {
            out.write(buffer, 0, count)
            count = `in`.read(buffer, 0, buffer.size)
        } while (count != -1)
        `in`.close()
        out.close()
        destFile.setLastModified(srcFile.lastModified())
    }

    @JvmStatic
    fun getPlugin(e: AnActionEvent): FileSyncPlugin {
        val project = e.getData(CommonDataKeys.PROJECT)
        return if (project == null) throw NullPointerException("can't get project from event!") else getInstance(project)
    }

    @JvmStatic
    fun getCurrentCopierThread(e: AnActionEvent): SynchronizerThread? {
        val plugin = getPlugin(e) ?: return null
        val currentConsole = plugin.consolePane.currentConsole ?: return null
        return currentConsole.thread
    }

    class DirectoriesFilter : FileFilter {
        override fun accept(f: File): Boolean {
            return f.isDirectory
        }
    }
}
