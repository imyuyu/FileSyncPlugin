package org.imyuyu.idea.plugins.filesync

import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.openapi.startup.StartupActivity

/**
 *
 * Startup Activity
 *
 * date: 2023/2/19 18:57
 *
 * @author Zhengyu Hu
 */
class ProjectStartupActivity : ProjectActivity {

    override suspend fun execute(project: Project) {
        FileSyncPlugin.getInstance(project).projectOpened()
    }
}