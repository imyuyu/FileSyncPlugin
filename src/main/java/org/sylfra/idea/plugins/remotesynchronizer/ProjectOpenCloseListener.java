package org.sylfra.idea.plugins.remotesynchronizer;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import org.jetbrains.annotations.NotNull;

public class ProjectOpenCloseListener implements ProjectManagerListener {

    @Override
    public void projectOpened(@NotNull Project project) {
        project.getService(FileSyncPlugin.class).projectOpened();
    }

    @Override
    public void projectClosed(@NotNull Project project) {
        project.getService(FileSyncPlugin.class).projectClosed();
    }
}
