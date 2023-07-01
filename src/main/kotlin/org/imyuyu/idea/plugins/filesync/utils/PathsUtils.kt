package org.imyuyu.idea.plugins.filesync.utils

import com.intellij.openapi.components.impl.stores.IProjectStore
import com.intellij.openapi.components.stateStore
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import java.io.File

/**
 * Utility methods for paths management
 */
object PathsUtils {
    const val PATTERN_PROJECT_DIR = "\$PROJECT_DIR$"
    @JvmStatic
    fun toModelPath(path: String?): String {
        return path!!.replace(File.separatorChar, '/')
    }

    fun toModelPath(f: File): String {
        return toModelPath(f.absolutePath)
    }

    @JvmStatic
    fun getVirtualFile(path: String?): VirtualFile? {
        return LocalFileSystem.getInstance().findFileByPath(path!!)
    }

    @JvmStatic
    fun isAncestor(childPath: String?, parentPath: String?): Boolean {
        var childFile: File? = File(childPath)
        val parentFile = File(parentPath)
        while (childFile != null) {
            if (childFile == parentFile) {
                return true
            }
            childFile = childFile.parentFile
        }
        return false
    }

    fun isAncestor(parent: VirtualFile, f: VirtualFile?): Boolean {
        var tmp = f
        while (tmp != null) {
            if (tmp == parent) {
                return true
            }
            tmp = tmp.parent
        }
        return false
    }

    fun getRelativePath(roots: Array<VirtualFile?>, f: VirtualFile): String {
        for (root in roots) {
            val s = VfsUtil.getRelativePath(f, root!!, '/')
            if (s != null) {
                return s
            }
        }
        return f.path
    }

    @JvmStatic
    fun getRelativePath(project: Project, path: String): String {
        var path = path
        val projectStore = project.stateStore as IProjectStore
        var projectDir = VirtualFileManager.getInstance().findFileByNioPath(projectStore.projectBasePath)
        path = path.replace(PATTERN_PROJECT_DIR, projectDir!!.path)
        if (path == projectDir.path) {
            return PATTERN_PROJECT_DIR + "/"
        }
        val buffer = StringBuilder()
        var relPath: String? = null
        while (projectDir != null && getRelativePath(path, projectDir.path).also { relPath = it } == null) {
            buffer.append("../")
            projectDir = projectDir.parent
        }

        // Unexpected
        return if (projectDir == null) {
            path
        } else PATTERN_PROJECT_DIR + "/" + buffer + relPath
    }

    private fun getRelativePath(path: String, basePath: String): String? {
        var path = path
        var basePath = basePath
        path = toModelPath(path)
        basePath = toModelPath(basePath)
        if (!basePath.endsWith("/")) basePath += "/"
        val pos = path.indexOf(basePath)
        return if (pos == -1) null else path.substring(basePath.length)
    }

    fun replaceJavaExtensionByClass(path: String): String {
        return path.substring(0, path.length - 4) + "class"
    }
}
