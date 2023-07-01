package org.imyuyu.idea.plugins.filesync.ui.tables

import org.imyuyu.idea.plugins.filesync.utils.ConfigPathsManager
import org.imyuyu.idea.plugins.filesync.utils.LabelsFactory
import org.imyuyu.idea.plugins.filesync.utils.PathsUtils.getRelativePath
import org.imyuyu.idea.plugins.filesync.utils.PathsUtils.toModelPath
import java.awt.Color
import java.awt.Component
import javax.swing.JTable
import javax.swing.ListSelectionModel
import javax.swing.table.AbstractTableModel
import javax.swing.table.DefaultTableCellRenderer

/**
 * Base table used to view paths
 */
open class AbstractPathTable(model: AbstractPathTableModel?) : JTable(model) {
    init {
        getTableHeader().reorderingAllowed = false
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
    }

    val data: List<Any>?
        get() = pathModel.getData()

    fun setData(data: MutableList<Any>?) {
        pathModel.setData(data)
    }

    val current: Any?
        get() = pathModel.getValueAt(selectedRow)

    fun add(o: Any) {
        pathModel.add(o)
    }

    fun updateCurrent(o: Any) {
        pathModel.update(selectedRow, o)
    }

    fun removeCurrent() {
        val row = selectedRow
        pathModel.remove(row)
        getSelectionModel().setSelectionInterval(row, row)
    }

    private val pathModel: AbstractPathTableModel
        private get() = model as AbstractPathTableModel

    /**
     * Implementation based on a List
     */
    abstract class AbstractPathTableModel protected constructor(protected var colNames: Array<String>) :
        AbstractTableModel() {

            @get:JvmName("getRawData")
            @set:JvmName("setRawData")
            protected var data: MutableList<Any>? = null
        fun getData(): List<Any>? {
            return data
        }

        fun setData(data: MutableList<Any>?) {
            this.data = data
            fireTableDataChanged()
        }

        override fun getRowCount(): Int {
            return if (data == null) 0 else data!!.size
        }

        override fun getColumnCount(): Int {
            return colNames.size
        }

        override fun getColumnName(i: Int): String {
            return colNames[i]
        }

        fun getValueAt(row: Int): Any? {
            return if (row > -1 && row < data!!.size) data!![row] else null
        }

        fun remove(row: Int) {
            if (row > -1 && row < data!!.size) {
                data!!.removeAt(row)
                fireTableRowsDeleted(row, row)
            }
        }

        fun add(o: Any) {
            data!!.add(o)
            fireTableRowsInserted(data!!.size, data!!.size)
        }

        fun update(row: Int, o: Any) {
            if (row > -1 && row < data!!.size) {
                data!![row] = o
                fireTableRowsUpdated(row, row)
            }
        }
    }

    /**
     * Add a tooltip and set a presentable path
     */
    protected class ToolTipCellRenderer : DefaultTableCellRenderer() {
        override fun getTableCellRendererComponent(
            table: JTable, value: Any,
            isSelected: Boolean, hasFocus: Boolean, row: Int, column: Int
        ): Component {
            val result = super.getTableCellRendererComponent(
                table,
                value,
                isSelected,
                hasFocus,
                row,
                column
            ) as DefaultTableCellRenderer
            toolTipText = result.text
            return result
        }
    }

    /**
     * Add a tooltip and set a presentable path
     */
    protected class PathCellRenderer(private val pathManager: ConfigPathsManager) : DefaultTableCellRenderer() {
        override fun getTableCellRendererComponent(
            table: JTable, value: Any,
            isSelected: Boolean, hasFocus: Boolean, row: Int, column: Int
        ): Component {
            val result = super.getTableCellRendererComponent(
                table,
                value,
                isSelected,
                hasFocus,
                row,
                column
            ) as DefaultTableCellRenderer
            var path: String? = toModelPath(result.text)
            path = if (pathManager.plugin.config.generalOptions.isStoreRelativePaths) {
                getRelativePath(pathManager.plugin.project, path!!)
            } else {
                pathManager.expandPath(path, false)
            }
            path = pathManager.toPresentablePath(path!!)
            result.text = path
            toolTipText = pathManager.expandPath(result.text, false)
            return result
        }
    }

    /**
     * Add a tooltip and handle non-relative paths
     */
    protected class RelativePathCellRenderer(private val pathManager: ConfigPathsManager) : DefaultTableCellRenderer() {
        override fun getTableCellRendererComponent(
            table: JTable, value: Any,
            isSelected: Boolean, hasFocus: Boolean, row: Int, column: Int
        ): Component {
            val result = super.getTableCellRendererComponent(
                table,
                value,
                isSelected,
                hasFocus,
                row,
                column
            ) as DefaultTableCellRenderer
            val path = result.text
            val inProject = pathManager.isRelativePath(path) || pathManager.isOutputPath(path)
            var presentablePath: String?
            presentablePath = if (pathManager.plugin.config.generalOptions.isStoreRelativePaths) {
                getRelativePath(pathManager.plugin.project, path)
            } else {
                pathManager.expandPath(path, false)
            }
            presentablePath = pathManager.toPresentablePath(presentablePath!!)
            result.text = presentablePath
            if (inProject) {
                result.foreground = Color.black
            } else {
                result.foreground = Color.red
                result.text = (result.text
                        + " (" + LabelsFactory[LabelsFactory.MSG_PATH_NOT_IN_PROJECT]
                        + ")")
            }
            toolTipText = pathManager.expandPath(presentablePath, false)
            return result
        }
    }
}
