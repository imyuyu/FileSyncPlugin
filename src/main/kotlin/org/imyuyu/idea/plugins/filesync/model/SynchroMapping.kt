package org.imyuyu.idea.plugins.filesync.model

/**
 * Mapping between a source path and a destination path used to specify
 * included paths
 */
class SynchroMapping : Cloneable {
    @JvmField
    var srcPath: String? = null
    @JvmField
    var destPath: String? = null
    var isDeleteObsoleteFiles = false

    constructor()
    constructor(
        destPath: String?,
        srcPath: String?, deleteObsoleteFiles: Boolean
    ) {
        this.destPath = destPath
        this.srcPath = srcPath
        isDeleteObsoleteFiles = deleteObsoleteFiles
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o !is SynchroMapping) return false
        val synchroMapping = o
        if (isDeleteObsoleteFiles != synchroMapping.isDeleteObsoleteFiles) return false
        if (if (destPath != null) destPath != synchroMapping.destPath else synchroMapping.destPath != null) return false
        return if (if (srcPath != null) srcPath != synchroMapping.srcPath else synchroMapping.srcPath != null) false else true
    }

    override fun hashCode(): Int {
        var result: Int
        result = if (srcPath != null) srcPath.hashCode() else 0
        result = 29 * result + if (destPath != null) destPath.hashCode() else 0
        result = 29 * result + if (isDeleteObsoleteFiles) 1 else 0
        return result
    }

    public override fun clone(): Any {
        return super.clone();
    }


}
