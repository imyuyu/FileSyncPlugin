package org.sylfra.idea.plugins.remotesynchronizer;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import org.jetbrains.annotations.NotNull;

/**
 *
 * <p>
 * date: 2023/2/19 18:57
 *
 * @author Zhengyu Hu
 */
public class ProjectStartupActivity implements StartupActivity {

    @Override
    public void runActivity(@NotNull Project project) {
        project.getService(FileSyncPlugin.class).projectOpened();
    }
}
