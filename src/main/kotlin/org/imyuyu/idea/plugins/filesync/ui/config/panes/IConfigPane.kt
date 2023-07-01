package org.imyuyu.idea.plugins.filesync.ui.config.panes

import org.imyuyu.idea.plugins.filesync.model.Config
import org.imyuyu.idea.plugins.filesync.utils.ConfigPathsManager

/**/
interface IConfigPane {
    val title: String
    fun isModified(config: Config): Boolean
    fun reset(config: Config)
    fun apply(config: Config)
    fun buildUI(pathsManager: ConfigPathsManager?)
}
