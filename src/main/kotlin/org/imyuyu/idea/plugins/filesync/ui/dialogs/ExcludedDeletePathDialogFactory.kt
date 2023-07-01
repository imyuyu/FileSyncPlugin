package org.imyuyu.idea.plugins.filesync.ui.dialogs

import org.imyuyu.idea.plugins.filesync.utils.ConfigPathsManager

/**/
class ExcludedDeletePathDialogFactory private constructor() : PathDialogFactory {
    override fun createDialog(pathManager: ConfigPathsManager, defaultValue: Any?): AbstractPathDialog {
        return ExcludedDeletePathDialog(pathManager, defaultValue)
    }

    companion object {
        @Volatile
        private var instance: ExcludedDeletePathDialogFactory? = null

        @JvmStatic
        fun getInstance(): ExcludedDeletePathDialogFactory {
            return instance ?: synchronized(this) {
                instance ?: ExcludedDeletePathDialogFactory().also { instance = it }
            }
        }
    }
}
