package org.imyuyu.idea.plugins.filesync.ui.dialogs

import org.imyuyu.idea.plugins.filesync.utils.ConfigPathsManager

/**/
interface PathDialogFactory {
    fun createDialog(pathManager: ConfigPathsManager, defaultValue: Any?): AbstractPathDialog
}
