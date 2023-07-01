package org.imyuyu.idea.plugins.filesync.javasupport

import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.vfs.VirtualFile
import org.imyuyu.idea.plugins.filesync.javasupport.IJavaSupport

/**
 */
class NoJavaSupport : IJavaSupport {
    override fun getSelectedFiles(dataContext: DataContext?): Array<VirtualFile> {
        return CommonDataKeys.VIRTUAL_FILE_ARRAY.getData(dataContext!!) ?: return emptyArray<VirtualFile>()
    }

    override fun insideModule(dataContext: DataContext?): Boolean {
        return false
    }

    override fun getClassFilePaths(f: VirtualFile?): List<String?>? {
        return emptyList<String>()
    }
}
