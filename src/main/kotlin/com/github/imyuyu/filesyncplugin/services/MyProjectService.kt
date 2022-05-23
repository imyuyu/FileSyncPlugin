package com.github.imyuyu.filesyncplugin.services

import com.intellij.openapi.project.Project
import com.github.imyuyu.filesyncplugin.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
