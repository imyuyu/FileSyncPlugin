package org.imyuyu.idea.plugins.filesync.ui.config

import com.intellij.ui.Gray
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPanel
import com.intellij.ui.components.JBScrollPane
import org.imyuyu.idea.plugins.filesync.model.Config
import org.imyuyu.idea.plugins.filesync.model.SynchroMapping
import org.imyuyu.idea.plugins.filesync.model.TargetMappings
import org.imyuyu.idea.plugins.filesync.utils.ConfigPathsManager
import org.imyuyu.idea.plugins.filesync.utils.LabelsFactory
import org.imyuyu.idea.plugins.filesync.ui.config.ActionsHolder.*
import org.imyuyu.idea.plugins.filesync.ui.dialogs.ExcludedCopyPathDialogFactory
import org.imyuyu.idea.plugins.filesync.ui.dialogs.ExcludedDeletePathDialogFactory
import org.imyuyu.idea.plugins.filesync.ui.dialogs.PathDialogFactory
import org.imyuyu.idea.plugins.filesync.ui.dialogs.SynchroMappingDialogFactory
import org.imyuyu.idea.plugins.filesync.ui.tables.AbstractPathTable
import org.imyuyu.idea.plugins.filesync.ui.tables.ExcludedCopyPathsTable
import org.imyuyu.idea.plugins.filesync.ui.tables.ExcludedDeletePathsTable
import org.imyuyu.idea.plugins.filesync.ui.tables.SynchroPathsTable
import java.awt.*
import java.util.*
import javax.swing.*
import javax.swing.event.ChangeListener

class TargetTab(
  val targetMappings: TargetMappings,
  tabbedPane: TargetTabbedPane, pathsManager: ConfigPathsManager,
  var initialPos: Int
) : JPanel() {
    private val cbActive: JCheckBox
    private val synchroTable: SynchroPathsTable
    private val excludedCopyTable: ExcludedCopyPathsTable
    private val excludedDeleteTable: ExcludedDeletePathsTable

    init {
        synchroTable = SynchroPathsTable(pathsManager)
        excludedCopyTable = ExcludedCopyPathsTable()
        excludedDeleteTable = ExcludedDeletePathsTable()
        cbActive = JBCheckBox(
            LabelsFactory[LabelsFactory.LB_ACTIVE],
            targetMappings.isActive
        )
        cbActive.addChangeListener(ChangeListener {
            val index = tabbedPane.indexOfComponent(this@TargetTab)
            tabbedPane.setForegroundAt(index, tabbedPane.getForegroundAt(index))
        })
        reset()
        buildUI(pathsManager)
    }

    fun isModified(config: Config?): Boolean {
        return ((cbActive.isSelected != targetMappings.isActive)
                || (Arrays.asList(*targetMappings.synchroMappings) != synchroTable.data)
                || (Arrays.asList(*targetMappings.excludedCopyPaths) != excludedCopyTable.data)
                || (Arrays.asList(*targetMappings.excludedDeletePaths) != excludedDeleteTable.data))
    }

    fun reset() {
        cbActive.isSelected = targetMappings.isActive
        synchroTable.setData(cloneList(targetMappings.synchroMappings))
        excludedCopyTable.setData(cloneList(targetMappings.excludedCopyPaths))
        excludedDeleteTable.setData(cloneList(targetMappings.excludedDeletePaths))
    }

    @Suppress("UNCHECKED_CAST")
    fun apply() {
        targetMappings.isActive = cbActive.isSelected
        targetMappings.synchroMappings = toArray(synchroTable.data!!)
        targetMappings.excludedCopyPaths = toArray(excludedCopyTable.data!!)
        targetMappings.excludedDeletePaths = toArray(excludedDeleteTable.data!!)
    }

    inline fun <reified T> toArray(list: List<*>): Array<T> {
        return (list as List<T>).toTypedArray()
    }

    private fun buildUI(pathsManager: ConfigPathsManager) {
        val pnActive: JPanel = JBPanel<JBPanel<*>>(BorderLayout())
        pnActive.add(cbActive, BorderLayout.WEST)
        val xInfo: JLabel = JBLabel(
            ("X : "
                    + LabelsFactory[LabelsFactory.LB_DELETE_OBSOLETE])
        )
        xInfo.font = xInfo.font.deriveFont(Font.ITALIC)
        pnActive.add(xInfo, BorderLayout.EAST)
        val pnIncluded = createTablePanel(
            LabelsFactory[LabelsFactory.PANEL_INCLUDED_PATHS],
            synchroTable, SynchroMappingDialogFactory.getInstance(), pathsManager
        )
        val pnExcludedCopy = createTablePanel(
            LabelsFactory[LabelsFactory.PANEL_EXCLUDED_COPY_PATHS],
            excludedCopyTable, ExcludedCopyPathDialogFactory.getInstance(), pathsManager
        )
        val pnExcludedDelete = createTablePanel(
            LabelsFactory[LabelsFactory.PANEL_EXCLUDED_DELETE_PATHS],
            excludedDeleteTable, ExcludedDeletePathDialogFactory.getInstance(), pathsManager
        )
        layout = GridBagLayout()
        val c = GridBagConstraints()
        c.gridy = 0
        c.gridx = 0
        c.gridwidth = 2
        c.weighty = 0.0
        c.weightx = 1.0
        c.fill = GridBagConstraints.BOTH
        add(pnActive, c)
        c.gridy++
        c.weighty = 1.0
        add(pnIncluded, c)
        c.gridy++
        c.gridwidth = 1
        add(pnExcludedCopy, c)
        c.gridx++
        add(pnExcludedDelete, c)
    }

    private fun createTablePanel(
      title: String, table: AbstractPathTable,
      dialogFactory: PathDialogFactory, pathsManager: ConfigPathsManager
    ): JPanel {
        // Paths table
        val pnTable: JPanel = JBPanel<JBPanel<*>>(BorderLayout())
        val scrollPane: JScrollPane = JBScrollPane(table)
        scrollPane.viewport.preferredSize =
            Dimension(pnTable.preferredSize.getHeight().toInt(), pnTable.preferredSize.getWidth().toInt())
        pnTable.add(scrollPane, BorderLayout.CENTER)

        // Buttons
        val bnAdd = JButton(AddTableItemAction(table, dialogFactory, pathsManager))
        val bnEdit = JButton(EditTableItemAction(table, dialogFactory, pathsManager))
        val bnRemove = JButton(RemoveTableItemAction(table))
        val height = bnAdd.preferredSize.height
        bnAdd.maximumSize = Dimension(Short.MAX_VALUE.toInt(), height)
        bnEdit.maximumSize = Dimension(Short.MAX_VALUE.toInt(), height)
        bnRemove.maximumSize = Dimension(Short.MAX_VALUE.toInt(), height)
        val pnButtons: JPanel = JBPanel<JBPanel<*>>()
        pnButtons.layout = BoxLayout(pnButtons, BoxLayout.Y_AXIS)
        pnButtons.border = BorderFactory.createEmptyBorder(5, 5, 5, 5)
        pnButtons.add(bnAdd)
        pnButtons.add(bnEdit)
        pnButtons.add(bnRemove)
        val pnMain: JPanel = JBPanel<JBPanel<*>>(BorderLayout())
        val titledBorder = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(
                JBColor.namedColor(
                    "Group.separatorColor", JBColor(
                        Gray.xCD, Gray.x51
                    )
                )
            ), title
        )
        pnMain.border = titledBorder
        //pnMain.setBorder(IdeBorderFactory.createTitledBorder(title, false, JBUI.insetsRight(10)));
        pnMain.add(pnTable, BorderLayout.CENTER)
        pnMain.add(pnButtons, BorderLayout.EAST)
        return pnMain
    }

    val isSetAsActive: Boolean
        get() = cbActive.isSelected

    companion object {
        fun <T> cloneList(array: Array<T>?): MutableList<Any> {
            if (array == null) return ArrayList()
            val result = ArrayList<Any>(array.size)
            for (anArray: Any? in array) {
                if (anArray is SynchroMapping) {
                    result.add(anArray.clone())
                } else {
                    result.add(anArray.toString())
                }
            }
            return result
        }
    }
}
