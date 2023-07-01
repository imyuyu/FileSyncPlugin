package org.imyuyu.idea.plugins.filesync.javasupport

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.CompilerModuleExtension
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiJavaFile
import com.intellij.psi.PsiManager
import org.imyuyu.idea.plugins.filesync.utils.PathsUtils

/**
 */
class DefaultJavaSupport protected constructor(private val project: Project) : IJavaSupport {
    override fun getSelectedFiles(dataContext: DataContext?): Array<VirtualFile> {
        val selectedFiles = LangDataKeys.VIRTUAL_FILE_ARRAY.getData(
            dataContext!!
        )
        if (selectedFiles == null) {
            val module = LangDataKeys.MODULE.getData(dataContext)
            return if (module == null) emptyArray() else ModuleRootManager.getInstance(module).contentRoots
        }
        return selectedFiles
    }

    override fun insideModule(dataContext: DataContext?): Boolean {
        return LangDataKeys.MODULE.getData(dataContext!!) != null
    }

    /**
     * Returns classes files path for java source file.
     * Several classes files may correspond to one java file due to inner classes
     *
     * @param f java source file
     * @return if main class file does not exist, a list containing its path.
     * Otherwise a list containing paths of main class and its existing inner
     * classes
     */
    override fun getClassFilePaths(f: VirtualFile?): List<String?>? {
        val outputPath = getOutputPath(f) ?: return null
        val psiManager = PsiManager.getInstance(project)
        val psiJavaFile = (psiManager.findFile(f!!) as PsiJavaFile?)!!
        val classes = psiJavaFile.classes

        // Each class defined in a source file may contain several inner class...
        val result: MutableList<String?> = ArrayList()
        for (aClass in classes) {
            val path = outputPath + '/' + aClass.qualifiedName!!.replace('.', '/') + ".class"
            val c = LocalFileSystem.getInstance().findFileByPath(path)
            result.addAll(getInnerClassFilePaths(c))
            result.add(path)
        }
        return result
    }

    /**
     * Return a list with all inner classes paths (anonymous or not) for the
     * specified class file
     */
    fun getInnerClassFilePaths(c: VirtualFile?): List<String?> {
        val result: MutableList<String?> = ArrayList()
        if (c != null) {
            val baseName = c.nameWithoutExtension + "$"
            val parent = c.parent
            val children = parent.children
            for (child in children) {
                if (child.nameWithoutExtension.indexOf(baseName) == 0) {
                    result.add(child.path)
                }
            }
        }
        return result
    }

    fun getRelativeSourcePath(f: VirtualFile): String {
        return PathsUtils.getRelativePath(
            ProjectRootManager.getInstance(project).contentSourceRoots, f
        )
    }

    fun getOutputPath(f: VirtualFile?): String? {
        val fileIndex = ProjectRootManager.getInstance(project)
            .fileIndex
        val module = fileIndex.getModuleForFile(f!!)
        val vFile: VirtualFile?
        vFile = if (fileIndex.isInTestSourceContent(f)) {
            CompilerModuleExtension.getInstance(module)!!.compilerOutputPathForTests
        } else if (fileIndex.isInSourceContent(f)) {
            CompilerModuleExtension.getInstance(module)!!.compilerOutputPath
        } else {
            null
        }
        return if (vFile == null) {
            null
        } else PathsUtils.toModelPath(vFile.presentableUrl)
    }

    fun getRelativePath(path: String): String {
        val f = PathsUtils.getVirtualFile(path) ?: return path
        return getRelativePath(f)
    }

    fun getRelativeOutputPath(path: String): String {
        val f = PathsUtils.getVirtualFile(path) ?: return path
        return getRelativePath(f)
    }

    fun getRelativePath(f: VirtualFile): String {
        return PathsUtils.getRelativePath(
            ProjectRootManager.getInstance(project).contentRoots, f
        )
    }
}
