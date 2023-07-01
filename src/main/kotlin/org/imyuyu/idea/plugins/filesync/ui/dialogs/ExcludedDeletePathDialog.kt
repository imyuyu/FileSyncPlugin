package org.imyuyu.idea.plugins.filesync.ui.dialogs

import org.imyuyu.idea.plugins.filesync.utils.ConfigPathsManager
import org.imyuyu.idea.plugins.filesync.utils.LabelsFactory

/**
 * Intended to get an excluded delete path
 */
class ExcludedDeletePathDialog(pathManager: ConfigPathsManager, defaultValue: Any?) : ExcludedPathDialog(
    pathManager,
    defaultValue,
    LabelsFactory[LabelsFactory.FRAME_EXCLUDED_DELETE_PATH],
    LabelsFactory[LabelsFactory.LB_EXCLUDED_DELETE_PATH]
) 