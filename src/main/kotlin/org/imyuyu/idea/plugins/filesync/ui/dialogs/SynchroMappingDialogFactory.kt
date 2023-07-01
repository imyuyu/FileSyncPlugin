/**
 */
package org.imyuyu.idea.plugins.filesync.ui.dialogs

import org.imyuyu.idea.plugins.filesync.utils.ConfigPathsManager

class SynchroMappingDialogFactory private constructor() : PathDialogFactory {
    override fun createDialog(pathManager: ConfigPathsManager, defaultValue: Any?): AbstractPathDialog {
        return SynchroMappingDialog(pathManager, defaultValue)
    }

    companion object {
        @Volatile
        private var instance: SynchroMappingDialogFactory? = null

        @JvmStatic
        fun getInstance(): SynchroMappingDialogFactory {
            return instance ?: synchronized(this) {
                instance ?: SynchroMappingDialogFactory().also { instance = it }
            }
        }
    }
}
