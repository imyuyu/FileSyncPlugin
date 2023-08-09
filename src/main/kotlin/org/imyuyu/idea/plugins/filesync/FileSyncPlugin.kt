package org.imyuyu.idea.plugins.filesync

import com.intellij.openapi.components.impl.stores.IProjectStore
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.project.stateStore
import org.imyuyu.idea.plugins.filesync.javasupport.IJavaSupport
import org.imyuyu.idea.plugins.filesync.javasupport.NoJavaSupport
import org.imyuyu.idea.plugins.filesync.model.Config
import org.imyuyu.idea.plugins.filesync.synchronizing.SynchronizerThreadManager
import org.imyuyu.idea.plugins.filesync.ui.ThreadConsolePane
import org.imyuyu.idea.plugins.filesync.utils.ConfigExternalizer
import org.imyuyu.idea.plugins.filesync.utils.ConfigPathsManager
import org.imyuyu.idea.plugins.filesync.utils.ConfigStateComponent
import java.util.*

class FileSyncPlugin(// Current project
  @JvmField val project: Project
) {

    // Provides support for paths management
    @JvmField
    val pathManager: ConfigPathsManager

    // Manage threaded copies
    @JvmField
    val copierThreadManager: SynchronizerThreadManager

    // Contains different consoles
    @JvmField
    val consolePane: ThreadConsolePane
    @JvmField
    var javaSupport: IJavaSupport?

    init {
        pathManager = ConfigPathsManager(this)
        javaSupport = project.getService(IJavaSupport::class.java)
        if (javaSupport == null) {
            javaSupport = NoJavaSupport()
        }
        consolePane = ThreadConsolePane(this)
        copierThreadManager = SynchronizerThreadManager(this)
    }

    val projectBaseDir: VirtualFile?
        get() {
            val projectStore = project.stateStore as IProjectStore
            return VirtualFileManager.getInstance().findFileByNioPath(projectStore.projectBasePath)
        }
    val config: Config
        get() = stateComponent.state

    fun projectOpened() {}
    fun projectClosed() {}
    val configExternalizer: ConfigExternalizer
        get() = project.getService(ConfigExternalizer::class.java)

    /**
     * Provides settings component
     *
     * @return settings component
     */
    val stateComponent: ConfigStateComponent
        get() = project.getService(ConfigStateComponent::class.java)

    fun launchSyncIfAllowed(files: Array<VirtualFile>) {
        // Check if configuration allows concurrent runs when a synchro is running
        if (!Objects.requireNonNull(stateComponent.state).generalOptions.isAllowConcurrentRuns
            && copierThreadManager.hasRunningSynchro()
        ) {
            consolePane.doPopup()
            return
        }
        copierThreadManager.launchSynchronization(files)
    }

    companion object {
        const val PLUGIN_NAME = "FileSync"
        const val WINDOW_ACTIONS_NAME = PLUGIN_NAME + "Window"
        const val ACTION_CLEAR_CONSOLE_NAME = "ConsoleClearAction"
        const val ACTION_REMOVE_CONSOLE_NAME = "ConsoleRemoveAction"
        const val ACTION_INTERRUPT_THREAD_NAME = "ThreadInterruptAction"
        const val ACTION_STOP_THREAD_NAME = "ThreadStopAction"
        const val ACTION_RERUN_LAST_SYNCHRO_NAME = "RerunLastSynchroAction"

        @JvmStatic
        fun getInstance(project: Project): FileSyncPlugin {
            return project.getService(FileSyncPlugin::class.java)
        }
    }
}
