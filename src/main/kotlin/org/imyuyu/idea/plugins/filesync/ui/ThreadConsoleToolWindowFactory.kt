package org.imyuyu.idea.plugins.filesync.ui

import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import org.imyuyu.idea.plugins.filesync.adaptedMessage
import org.imyuyu.idea.plugins.filesync.FileSyncPlugin

/**
 *
 *
 * date: 2023/2/12 16:45
 *
 * @author Zhengyu Hu
 */
class ThreadConsoleToolWindowFactory : ToolWindowFactory, DumbAware {


    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        if (project.isDisposed) {
            return
        }

        val fileSyncPlugin = FileSyncPlugin.getInstance(project)
        val contentFactory = ContentFactory.getInstance()
        val content = contentFactory.createContent(
            ToolPanel(fileSyncPlugin.consolePane, fileSyncPlugin.config),
            adaptedMessage("TITLE_CONSOLE"), true
        )
        toolWindow.contentManager.addContent(content)

        //toolWindow.setIcon(new ImageIcon(FileSyncPlugin.getResource("logo-small.png")));
    }

    override fun init(toolWindow: ToolWindow) {
        adaptedMessage("FileSync").let { title ->
            toolWindow.title = title
            toolWindow.stripeTitle = title
        }
    }
}
