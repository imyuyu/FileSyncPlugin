package org.imyuyu.idea.plugins.filesync.ui.tables

import org.imyuyu.idea.plugins.filesync.utils.LabelsFactory

/**
 * Contains one column for excluded copy paths
 */
class ExcludedCopyPathsTable : AbstractPathTable(PrivateTableModel()) {
    init {
        getColumnModel().getColumn(0).cellRenderer = ToolTipCellRenderer()
    }

    private class PrivateTableModel : AbstractPathTableModel(COLUMN_NAMES) {
        override fun getValueAt(row: Int, col: Int): Any {
            return getValueAt(row)!!
        }
    }

    companion object {
        // Columns names
        private val COLUMN_NAMES = arrayOf(
            LabelsFactory[LabelsFactory.COL_EXCLUDED_COPY_PATH]
        )
    }
}
