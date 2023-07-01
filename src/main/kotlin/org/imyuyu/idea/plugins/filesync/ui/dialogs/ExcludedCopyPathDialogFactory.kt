package org.imyuyu.idea.plugins.filesync.ui.dialogs

import org.imyuyu.idea.plugins.filesync.utils.ConfigPathsManager

/**/
class ExcludedCopyPathDialogFactory private constructor() : PathDialogFactory {
    override fun createDialog(pathManager: ConfigPathsManager, defaultValue: Any?): AbstractPathDialog {
        return ExcludedCopyPathDialog(pathManager, defaultValue)
    }

    companion object {
        @Volatile
        private var instance: ExcludedCopyPathDialogFactory? = null

        @JvmStatic
        fun getInstance(): ExcludedCopyPathDialogFactory {
            return instance ?: synchronized(this) {
                instance ?: ExcludedCopyPathDialogFactory().also { instance = it }
            }
        }
    }
}
