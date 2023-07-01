package org.imyuyu.idea.plugins.filesync.model

import java.io.Serializable

class TargetMappings : Serializable {
    @JvmField
    var name: String? = null
    var isActive = true
    @JvmField
    var synchroMappings: Array<SynchroMapping?>
    @JvmField
    var excludedCopyPaths: Array<String>
    @JvmField
    var excludedDeletePaths: Array<String>

    init {
        synchroMappings = emptyArray()
        excludedCopyPaths = DEFAULT_EXCLUDES_COPY
        excludedDeletePaths = emptyArray()
    }

    companion object {
        val DEFAULT_EXCLUDES_COPY = arrayOf(
            "**/.dependency-info/*",
            "**/.git/*",
            "**/.svn/*",
            "**/CVS/*",
            "**/*.java"
        )
    }
}
