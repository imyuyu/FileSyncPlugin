package org.imyuyu.idea.plugins.filesync

import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity

/**
 *
 * Startup Activity
 *
 * date: 2023/2/19 18:57
 *
 * @author Zhengyu Hu
 */
class ProjectStartupActivity : StartupActivity {

    override fun runActivity(project: Project) {
        project.getService(FileSyncPlugin::class.java).projectOpened()
    }
}