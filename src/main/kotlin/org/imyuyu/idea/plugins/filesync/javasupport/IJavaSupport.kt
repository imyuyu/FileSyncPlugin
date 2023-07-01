package org.imyuyu.idea.plugins.filesync.javasupport

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.vfs.VirtualFile

/**
 */
interface IJavaSupport {
    fun getSelectedFiles(dataContext: DataContext?): Array<VirtualFile>
    fun insideModule(dataContext: DataContext?): Boolean
    fun getClassFilePaths(f: VirtualFile?): List<String?>?
}
