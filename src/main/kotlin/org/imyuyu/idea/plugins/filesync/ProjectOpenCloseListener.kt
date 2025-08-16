package org.imyuyu.idea.plugins.filesync

import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManagerListener

class ProjectOpenCloseListener : ProjectManagerListener {
    override fun projectClosed(project: Project) {
        FileSyncPlugin.getInstance(project).projectClosed()
    }
}
