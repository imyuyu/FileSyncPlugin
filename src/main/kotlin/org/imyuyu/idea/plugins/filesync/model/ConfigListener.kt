package org.imyuyu.idea.plugins.filesync.model

/**
 * Used when config change
 */
interface ConfigListener {
    fun configChanged(config: Config?)
}
