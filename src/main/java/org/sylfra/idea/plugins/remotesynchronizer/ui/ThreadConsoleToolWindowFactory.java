package org.sylfra.idea.plugins.remotesynchronizer.ui;

import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;
import org.sylfra.idea.plugins.remotesynchronizer.FileSyncPlugin;

import javax.swing.*;

/**
 * <p>
 * date: 2023/2/12 16:45
 *
 * @author Zhengyu Hu
 */
public class ThreadConsoleToolWindowFactory implements ToolWindowFactory, DumbAware {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        FileSyncPlugin fileSyncPlugin = FileSyncPlugin.getInstance(project);

        ContentFactory contentFactory = ContentFactory.getInstance();
        Content content = contentFactory.createContent(new ToolPanel(fileSyncPlugin.getConsolePane(), fileSyncPlugin.getConfig()),
                FileSyncPlugin.PLUGIN_NAME + " Console", true);
        toolWindow.getContentManager().addContent(content);

        //toolWindow.setIcon(new ImageIcon(FileSyncPlugin.getResource("logo-small.png")));
    }
}
