package org.imyuyu.idea.plugins.filesync.ui

import com.intellij.openapi.wm.ToolWindowManager
import org.imyuyu.idea.plugins.filesync.FileSyncPlugin
import org.imyuyu.idea.plugins.filesync.synchronizing.SynchronizerThread
import org.imyuyu.idea.plugins.filesync.utils.LabelsFactory
import javax.swing.JTabbedPane
import javax.swing.SwingUtilities

/**
 * Thread consoles container
 */
class ThreadConsolePane(protected val plugin: FileSyncPlugin) : JTabbedPane() {

    fun createConsole(
        plugin: FileSyncPlugin,
        copierThread: SynchronizerThread, findPosition: Boolean
    ): ThreadConsole {
        val consoleName = findConsoleName(copierThread)
        val console = ThreadConsole(
            this, plugin.config, copierThread,
            consoleName
        )
        plugin.config.addConfigListener(console)
        console.setMainConsole(findMainConsole(console))
        copierThread.console = console
        addConsole(console, findPosition)
        return console
    }

    fun addConsole(console: ThreadConsole?, findPosition: Boolean) {
        console!!.title = findConsoleName(console.thread)
        val index = if (findPosition) findConsolePosition(console) else componentCount
        insertTab(findTitle(console), null, console, null, index)
    }

    private fun findConsolePosition(console: ThreadConsole): Int {
        val mainConsole = findMainConsole(console) ?: return componentCount
        val start = indexOfComponent(mainConsole)
        for (i in start + 1 until componentCount) {
            val c = getComponentAt(i) as ThreadConsole
            if (!c.hasSameTargetMappings(console)) return i
        }
        return componentCount
    }

    private fun findMainConsole(console: ThreadConsole): ThreadConsole? {
        for (i in 0 until componentCount) {
            val c = getComponentAt(i) as ThreadConsole
            if (c.isMainConsole() && c.hasSameTargetMappings(console)) return c
        }
        return null
    }

    private fun findConsoleName(thread: SynchronizerThread?): String? {
        var found = false
        var result = thread!!.targetMappings.name
        for (i in 0 until componentCount) {
            val console = getComponentAt(i) as ThreadConsole
            if (console.title == result) {
                found = true
                break
            }
        }
        if (!found) return result
        var inc = 1
        result = thread.targetMappings.name + " (" + inc + ")"
        var i = 0
        while (i < componentCount) {
            val console = getComponentAt(i) as ThreadConsole
            if (console.title == result) {
                i = 0
                result = thread.targetMappings.name + " (" + ++inc + ")"
            }
            i++
        }
        return result
    }

    fun getConsole(i: Int): ThreadConsole {
        return getComponentAt(i) as ThreadConsole
    }

    val currentConsole: ThreadConsole
        get() = selectedComponent as ThreadConsole

    fun removeCurrentConsole(): ThreadConsole {
        val console = currentConsole
        remove(console)
        return console
    }

    fun removeConsole(thread: SynchronizerThread) {
        remove(thread.console)
    }

    fun updateTitle(console: ThreadConsole) {
        SwingUtilities.invokeLater { setTitleAt(indexOfComponent(console), findTitle(console)) }
    }

    private fun findTitle(console: ThreadConsole): String? {
        var title = console.title
        if (console.thread.isActive) title += " " + LabelsFactory[LabelsFactory.TITLE_CONSOLE_BUSY] else if (console.thread.isInterrupted) title += (" "
                + LabelsFactory[LabelsFactory.TITLE_CONSOLE_INTERRUPTED])
        return title
    }

    fun doPopup() {
        ToolWindowManager.getInstance(plugin.project)
            .getToolWindow(FileSyncPlugin.PLUGIN_NAME)
            ?.show(null)
    }
}
