package org.imyuyu.idea.plugins.filesync.utils

import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.roots.CompilerModuleExtension
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.VirtualFile
import org.imyuyu.idea.plugins.filesync.FileSyncPlugin
import org.imyuyu.idea.plugins.filesync.model.Config
import org.imyuyu.idea.plugins.filesync.model.ConfigListener
import org.imyuyu.idea.plugins.filesync.model.TargetMappings
import java.io.File
import java.io.IOException
import java.util.*

/**
 * Provides support for paths management, using current project and plugin
 * settings
 *
 *
 * NB : paths are handled with '/'
 */
class ConfigPathsManager(@JvmField val plugin: FileSyncPlugin) {
    private val pathsCacheManager: PathsCacheManager

    init {
        pathsCacheManager = PathsCacheManager(plugin.config)
        plugin.config.addConfigListener(pathsCacheManager)
    }

    fun isRelativePath(path: String?): Boolean {
        return isRelativePath(
            ProjectRootManager.getInstance(plugin.project).contentRoots, path
        )
    }

    fun isRelativePath(roots: Array<VirtualFile>, path: String?): Boolean {
        for (root in roots) {
            if (isRelativePath(root.path, path)) {
                return true
            }
        }
        return false
    }

    fun isRelativePath(root: String?, path: String?): Boolean {
        var path = path
        path = expandPath(path, true)
        return (path!!.lowercase(Locale.getDefault()).indexOf(root!!.lowercase(Locale.getDefault())) == 0
                && (path.length == root.length || path[root.length] == '/'))
    }

    fun isOutputPath(path: String?): Boolean {
        val modules = ModuleManager.getInstance(plugin.project).modules
        for (module in modules) {
            val cme = CompilerModuleExtension.getInstance(module) ?: return false

            // Use getCompilerOutputXXXPointer since getCompilerOutputXXXPath return NULL when directory does not exist
            if ((cme.compilerOutputPointer != null
                        && isRelativePath(PathsUtils.toModelPath(cme.compilerOutputPointer.presentableUrl), path))
                || (cme.compilerOutputForTestsPointer != null
                        && isRelativePath(
                    PathsUtils.toModelPath(cme.compilerOutputForTestsPointer.presentableUrl),
                    path
                ))
            ) {
                return true
            }
        }
        return false
    }

    fun isJavaSource(f: VirtualFile): Boolean {
        if (!f.name.endsWith(".java")) return false
        val vf = ProjectRootManager.getInstance(plugin.project).contentSourceRoots
        for (aVf in vf) {
            if (PathsUtils.isAncestor(aVf, f)) {
                return true
            }
        }
        return false
    }

    val projectDefaultRoot: VirtualFile?
        get() {
            val vf = ProjectRootManager.getInstance(plugin.project)
                .contentRoots
            return if (vf.size == 0) plugin.projectBaseDir else vf[0]
        }

    fun isExcludedFromCopy(target: TargetMappings, path: String): Boolean {
        return isExcluded(target.excludedCopyPaths, path)
    }

    fun isExcludedFromDeletion(target: TargetMappings, path: String): Boolean {
        return isExcluded(target.excludedDeletePaths, path)
    }

    private fun isExcluded(paths: Array<String>, path: String): Boolean {
        var path: String = path
        for (itPath in paths) {
            val f = File(path)
            if (f.isDirectory) {
                path += "/"
            }
            if (SelectorUtils.match(itPath, path)) {
                return true
            }
        }
        return false
    }

    fun getRemotePath(target: TargetMappings, path: String): String? {
        var result = pathsCacheManager.getRemotePath(target, path)
        if (result != null) return if (PathsCacheManager.NULL_PATH == result) null else result
        if (isExcludedFromCopy(target, path)) {
            pathsCacheManager.storeRemotePath(
                target, path,
                PathsCacheManager.NULL_PATH
            )
            return null
        }
        result = findRemotePath(target, path)
        pathsCacheManager.storeRemotePath(
            target, path,
            result ?: PathsCacheManager.NULL_PATH
        )
        return result
    }

    private fun findRemotePath(
        target: TargetMappings,
        path: String?
    ): String? {
        var bestPath: String? = null
        var destPath: String? = null

        // Find best included path
        for (i in target.synchroMappings.indices) {
            val pathMapping = target.synchroMappings[i]!!
            if (matchesBetter(path, pathMapping.srcPath, bestPath)) {
                bestPath = expandPath(pathMapping.srcPath, true)
                destPath = expandPath(pathMapping.destPath, true)
            }
        }
        return buildPathFromBestPath(bestPath, path, destPath)
    }

    fun getSrcPath(target: TargetMappings, path: String): String? {
        var result = pathsCacheManager.getSrcPath(target, path)
        if (result != null) return if (PathsCacheManager.NULL_PATH == result) null else result
        if (isExcludedFromDeletion(target, path)) {
            pathsCacheManager.storeSrcPath(
                target, path,
                PathsCacheManager.NULL_PATH
            )
            return null
        }
        var bestPath: String? = null
        for (i in target.synchroMappings.indices) {
            val pathMapping = target.synchroMappings[i]!!
            val destPath = expandPath(pathMapping.destPath, true)
            if (path.indexOf(destPath!!) == 0) {
                val srcPath = expandPath(pathMapping.srcPath, true)
                val tmp = buildPathFromBestPath(destPath, path, srcPath)

                // check is this path is not precisely linked
                if (path == findRemotePath(target, tmp)
                    && matchesBetter(path, destPath, bestPath)
                ) {
                    result = tmp
                    bestPath = destPath
                }
            }
        }
        pathsCacheManager.storeSrcPath(
            target, path,
            result ?: PathsCacheManager.NULL_PATH
        )
        return result
    }

    private fun buildPathFromBestPath(
        bestPath: String?, paramPath: String?,
        foundPath: String?
    ): String? {
        var paramPath = paramPath
        var foundPath = foundPath
        if (bestPath == null) return null

        // Build absolute src path
        if (paramPath!!.lowercase(Locale.getDefault())
                .indexOf(bestPath.lowercase(Locale.getDefault())) != 0
        ) return null
        if (foundPath!![foundPath.length - 1] == '/') {
            val i = paramPath.lastIndexOf('/')
            return if (i == -1 && paramPath.length > 1) null else foundPath + paramPath.substring(i + 1)
        }
        paramPath = paramPath.substring(bestPath.length)
        if ("" != paramPath && paramPath[0] != '/' && foundPath[foundPath.length - 1] != '/') {
            foundPath += '/'
        }
        return foundPath + paramPath
    }

    fun toPresentablePath(path: String): String {
        var path = path
        path = path.replace('/', File.separatorChar)
        return path
    }

    fun expandPath(path: String?, modelPath: Boolean): String? {
        var path = path
        if (path!!.startsWith(PathsUtils.PATTERN_PROJECT_DIR)) {
            path = (plugin.projectBaseDir!!.presentableUrl
                    + path.substring(PATTERN_PROJECT_DIR_LENGTH))
            try {
                path = File(path).canonicalPath
            } catch (e: IOException) {
                // ignored, can't do more and logging is not relevant
            }
        }
        if (modelPath) path = PathsUtils.toModelPath(path)
        return path
    }

    fun matchesBetter(path: String?, testPath: String?, bestPath: String?): Boolean {
        var testPath = testPath
        testPath = expandPath(testPath, true)
        if (testPath == null || path!!.lowercase(Locale.getDefault())
                .indexOf(testPath.lowercase(Locale.getDefault())) != 0
        ) {
            return false
        }
        if (testPath.length < path.length) {
            val c = path[testPath.length]
            if (testPath[testPath.length - 1] != '/' && c != '/') {
                return false
            }
        }
        return bestPath == null || testPath.length > bestPath.length
    }

    private class PathsCacheManager(config: Config?) : ConfigListener {
        private val caches: MutableMap<TargetMappings, Array<MutableMap<String, String>>>

        init {
            caches = HashMap()
            configChanged(config)
        }

        override fun configChanged(config: Config?) {
            caches.clear()
            for (targetMappings in config!!.getTargetMappings()) {
                caches[targetMappings] = arrayOf<MutableMap<String, String>>(
                    HashMap(),
                    HashMap()
                )
            }
        }

        @Synchronized
        fun getRemotePath(target: TargetMappings, path: String): String? {
            return caches[target]!![0][path]
        }

        @Synchronized
        fun getSrcPath(target: TargetMappings, path: String): String? {
            return caches[target]!![1][path]
        }

        @Synchronized
        fun storeRemotePath(
            target: TargetMappings, path: String,
            remotePath: String
        ) {
            caches[target]!![0][path] = remotePath
        }

        @Synchronized
        fun storeSrcPath(
            target: TargetMappings, path: String,
            srcPath: String
        ) {
            caches[target]!![1][path] = srcPath
        }

        companion object {
            const val NULL_PATH = "<null>"
        }
    }

    companion object {
        private const val PATTERN_PROJECT_DIR_LENGTH = PathsUtils.PATTERN_PROJECT_DIR.length
    }
}
