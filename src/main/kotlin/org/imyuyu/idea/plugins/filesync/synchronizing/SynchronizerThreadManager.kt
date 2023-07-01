package org.imyuyu.idea.plugins.filesync.synchronizing

import com.intellij.openapi.vfs.VirtualFile
import org.imyuyu.idea.plugins.filesync.FileSyncPlugin
import org.imyuyu.idea.plugins.filesync.model.Config
import org.imyuyu.idea.plugins.filesync.model.ConfigListener
import org.imyuyu.idea.plugins.filesync.model.TargetMappings

/**
 * Maintains a [SynchronizerThread] objects list.
 *
 *
 * When a new synchronizing action is invoked, an available thread is required.
 * If no thread is available (ie. all threads are running), a new thread is
 * created
 */
class SynchronizerThreadManager(private val plugin: FileSyncPlugin) : ConfigListener {
    private var threads: MutableList<SynchronizerThread?>

    init {
        plugin.config.addConfigListener(this)
        threads = ArrayList()
        updateThreads()
    }

    /**
     * Invoked from an synchronization acton
     */
    fun launchSynchronization(files: Array<VirtualFile>) {
        for (targetMappings in plugin.config.getTargetMappings()) {
            if (targetMappings.isActive) {
                val thread = getNotNullAvailableThread(targetMappings)
                thread.start(files)
            }
        }
    }

    /**
     * If no thread is available (ie. all threads are running), a new thread
     * is created
     */
    private fun getNotNullAvailableThread(target: TargetMappings): SynchronizerThread {
        var result = getAvailableThread(target)
        if (result != null) return result
        result = SynchronizerThread(plugin, target)
        plugin.consolePane.createConsole(plugin, result, true)
        threads.add(result)
        return result
    }

    fun getAvailableThread(target: TargetMappings): SynchronizerThread? {
        var result: SynchronizerThread?
        for (thread in threads) {
            result = thread
            if (result!!.targetMappings == target && result!!.isAvailable) {
                return result
            }
        }
        return null
    }

    fun hasRunningSynchro(): Boolean {
        var result: SynchronizerThread?
        for (thread in threads) {
            result = thread
            if (!result!!.isAvailable) {
                return true
            }
        }
        return false
    }

    fun removeThread(thread: SynchronizerThread?) {
        threads.remove(thread)
    }

    /**
     * Update thread list after configuration has changed
     */
    private fun updateThreads() {
        val newThreads: MutableList<SynchronizerThread?> = ArrayList()
        val consolePane = plugin.consolePane
        val selectedTab = consolePane.selectedComponent
        consolePane.removeAll()

        // First add threads from config mappings, trying to retrieve existing
        // threads
        for (targetMappings in plugin.config.getTargetMappings()) {
            if (!targetMappings.isActive) {
                continue
            }

            // Try to retrieve existing threads (and their console)
            var thread: SynchronizerThread? = null
            var found = false
            val itThreads = threads.iterator()
            while (itThreads.hasNext()) {
                thread = itThreads.next()
                if (thread!!.targetMappings == targetMappings) {
                    newThreads.add(thread)
                    itThreads.remove()
                    found = true
                    break
                }
            }
            if (found) {
                consolePane.addConsole(thread!!.console, false)
            } else {
                thread = SynchronizerThread(plugin, targetMappings)
                consolePane.createConsole(plugin, thread, false)
                newThreads.add(thread)
            }
        }

        // Second, trying to retreive "secondary" threads, ie those which were
        // created because their main thread was busy
        for (thread in threads) {
            if (thread!!.targetMappings.isActive
                && plugin.config.getTargetMappings()
                    .contains(thread.targetMappings)
            ) {
                newThreads.add(thread)
                consolePane.addConsole(thread.console, true)
            }
        }
        val i = consolePane.indexOfComponent(selectedTab)
        consolePane.selectedIndex = if (i > -1) i else if (consolePane.componentCount == 0) -1 else 0
        threads = newThreads
    }

    override fun configChanged(config: Config?) {
        updateThreads()
    }
}
