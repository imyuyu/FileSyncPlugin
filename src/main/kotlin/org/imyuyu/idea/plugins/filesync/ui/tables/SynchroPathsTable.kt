package org.imyuyu.idea.plugins.filesync.ui.tables

import org.imyuyu.idea.plugins.filesync.model.SynchroMapping
import org.imyuyu.idea.plugins.filesync.utils.ConfigPathsManager
import org.imyuyu.idea.plugins.filesync.utils.LabelsFactory

/**
 * Contains 2 columns for included paths : source path and destination path
 */
class SynchroPathsTable(pathManager: ConfigPathsManager) : AbstractPathTable(PrivatePathTableModel()) {
    init {
        getColumnModel().getColumn(NUM_COL_SRC_PATH).cellRenderer = RelativePathCellRenderer(pathManager)
        getColumnModel().getColumn(NUM_COL_DEST_PATH).cellRenderer =
            PathCellRenderer(pathManager)
        getColumnModel().getColumn(NUM_COL_DELETE_OBSOLETE).maxWidth = 50
    }

    private class PrivatePathTableModel : AbstractPathTableModel(COLUMN_NAMES) {
        override fun getColumnClass(columnIndex: Int): Class<*> {
            return if (columnIndex == NUM_COL_DELETE_OBSOLETE) Boolean::class.java else String::class.java
        }

        override fun getValueAt(row: Int, col: Int): Any? {
            val p = getValueAt(row) as SynchroMapping ?: return null
            when (col) {
                NUM_COL_SRC_PATH -> return p.srcPath!!
                NUM_COL_DEST_PATH -> return p.destPath!!
                NUM_COL_DELETE_OBSOLETE -> return p.isDeleteObsoleteFiles
            }
            return null
        }
    }

    companion object {
        // Columns indexes
        const val NUM_COL_SRC_PATH = 0
        const val NUM_COL_DEST_PATH = 1
        const val NUM_COL_DELETE_OBSOLETE = 2

        // Columns names
        private val COLUMN_NAMES = arrayOf(
            LabelsFactory[LabelsFactory.COL_SRC_PATH],
            LabelsFactory[LabelsFactory.COL_DEST_PATH],
            "X"
        )
    }
}
