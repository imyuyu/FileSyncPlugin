package org.imyuyu.idea.plugins.filesync.ui.config

import com.intellij.icons.AllIcons
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.vfs.VirtualFile
import icons.FileSyncIcons
import org.imyuyu.idea.plugins.filesync.FileSyncPlugin
import org.imyuyu.idea.plugins.filesync.FileSyncPlugin.Companion.getInstance
import org.imyuyu.idea.plugins.filesync.utils.ConfigPathsManager
import org.imyuyu.idea.plugins.filesync.utils.LabelsFactory
import org.imyuyu.idea.plugins.filesync.ui.dialogs.PathDialogFactory
import org.imyuyu.idea.plugins.filesync.ui.dialogs.SynchroMappingDialog
import org.imyuyu.idea.plugins.filesync.ui.tables.AbstractPathTable
import java.awt.Component
import java.awt.event.*
import java.io.File
import javax.swing.AbstractAction
import javax.swing.Icon
import javax.swing.JFileChooser
import javax.swing.event.ChangeEvent
import javax.swing.event.ChangeListener
import javax.swing.event.ListSelectionEvent
import javax.swing.event.ListSelectionListener
import javax.swing.filechooser.FileFilter

class ActionsHolder {
    class EditTableItemAction(
      private val table: AbstractPathTable,
      private val dialogFactory: PathDialogFactory,
      private val pathManager: ConfigPathsManager
    ) : AbstractAction(LabelsFactory[LabelsFactory.BN_EDIT_PATH]), ListSelectionListener {
        init {
            table.selectionModel.addListSelectionListener(this)
            table.addMouseListener(object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent) {
                    if (e.clickCount == 2) {
                        actionPerformed(null)
                    }
                }
            })
            isEnabled = table.selectedRow > -1
        }

        override fun actionPerformed(e: ActionEvent?) {
            val dialog = dialogFactory.createDialog(pathManager, table.current)
            try {
                dialog.show()
                if (dialog.exitCode == DialogWrapper.OK_EXIT_CODE) {
                    table.updateCurrent(dialog.value!!)
                }
            } finally {
                Disposer.dispose(dialog)
            }
        }

        override fun valueChanged(e: ListSelectionEvent) {
            isEnabled = table.selectedRow > -1 && table.rowCount > 0
        }
    }

    /**
     * Add Button (add an included/excluded path)
     */
    class AddTableItemAction(
      private val table: AbstractPathTable,
      private val dialogFactory: PathDialogFactory,
      private val pathManager: ConfigPathsManager
    ) : AbstractAction(LabelsFactory[LabelsFactory.BN_ADD_PATH]) {
        override fun actionPerformed(e: ActionEvent) {
            val dialog = dialogFactory.createDialog(pathManager, null)
            try {
                dialog.show()
                if (dialog.exitCode == DialogWrapper.OK_EXIT_CODE) {
                    table.add(dialog.value!!)
                    table.selectionModel
                        .setSelectionInterval(table.rowCount - 1, table.rowCount - 1)
                }
            } finally {
                Disposer.dispose(dialog)
            }
        }
    }

    /**
     * Remove Button (remove an included/excluded path)
     */
    class RemoveTableItemAction(private val table: AbstractPathTable) :
        AbstractAction(LabelsFactory[LabelsFactory.BN_REMOVE_PATH]), ListSelectionListener {
        init {
            table.selectionModel.addListSelectionListener(this)
            isEnabled = table.selectedRow > -1
        }

        override fun actionPerformed(e: ActionEvent) {
            table.removeCurrent()
            if (table.rowCount > 0) {
                table.selectionModel
                    .setSelectionInterval(table.rowCount - 1, table.rowCount - 1)
            }
        }

        override fun valueChanged(e: ListSelectionEvent) {
            isEnabled = table.selectedRow > -1 && table.rowCount > 0
        }
    }

    abstract class TargetTabbedPaneAction(
        name: String?, icon: Icon?,
        targetTabs: TargetTabbedPane
    ) : AbstractAction(name, icon), ChangeListener, ContainerListener {
        protected var targetTabs: TargetTabbedPane

        init {
            super.putValue(LONG_DESCRIPTION, name)
            this.targetTabs = targetTabs
            targetTabs.model.addChangeListener(this)
            targetTabs.addContainerListener(this)
        }

        override fun componentAdded(e: ContainerEvent) {
            checkEnabled()
        }

        override fun componentRemoved(e: ContainerEvent) {
            checkEnabled()
        }

        override fun stateChanged(e: ChangeEvent) {
            checkEnabled()
        }

        protected abstract fun checkEnabled()
    }

    class AddTabAction(targetTabs: TargetTabbedPane) : TargetTabbedPaneAction(
        LabelsFactory[LabelsFactory.BN_ADD_TARGET], AllIcons.General.Add,
        targetTabs
    ) {
        override fun actionPerformed(e: ActionEvent) {
            targetTabs.addTarget()
        }

        override fun checkEnabled() {}
    }

    class RemoveTabAction(targetTabs: TargetTabbedPane) : TargetTabbedPaneAction(
        LabelsFactory[LabelsFactory.BN_REMOVE_TARGET],
        AllIcons.General.Remove,
        targetTabs
    ) {
        override fun actionPerformed(e: ActionEvent) {
            targetTabs.removeTarget()
        }

        override fun checkEnabled() {
            isEnabled = targetTabs.componentCount > 1
        }
    }

    class MoveTabToLeftAction(targetTabs: TargetTabbedPane) : TargetTabbedPaneAction(
        LabelsFactory[LabelsFactory.BN_MOVE_TARGET_TO_LEFT],
        FileSyncIcons.TO_LEFT_ICON,
        targetTabs
    ) {
        override fun actionPerformed(e: ActionEvent) {
            targetTabs.moveTargetToLeft()
        }

        override fun checkEnabled() {
            isEnabled = targetTabs.selectedIndex > 0
        }
    }

    class MoveTabToRightAction(targetTabs: TargetTabbedPane) : TargetTabbedPaneAction(
        LabelsFactory[LabelsFactory.BN_MOVE_TARGET_TO_RIGHT],
        FileSyncIcons.TO_RIGHT_ICON,
        targetTabs
    ) {
        override fun actionPerformed(e: ActionEvent) {
            targetTabs.moveTargetToRight()
        }

        override fun checkEnabled() {
            isEnabled = (targetTabs.selectedIndex
                    < targetTabs.componentCount - 1)
        }
    }

    class ExportAction(private val configPanel: ConfigPanel) : AbstractAction(LabelsFactory[LabelsFactory.BN_EXPORT]) {
        private var fileChooser: JFileChooser? = null
        override fun actionPerformed(e: ActionEvent) {
            val project = configPanel.currentProject
            if (fileChooser == null) {
                fileChooser = JFileChooser()
                fileChooser!!.selectedFile = File(
                    project.guessProjectDir()?.path,
                    project.name + "-FileSync.xml"
                )
                fileChooser!!.fileFilter = object : FileFilter() {
                    override fun accept(file: File): Boolean {
                        return file.isDirectory || file.name.endsWith(".xml")
                    }

                    override fun getDescription(): String {
                        return "XML files"
                    }
                }
            }
            if (fileChooser!!.showSaveDialog(e.source as Component) != JFileChooser.APPROVE_OPTION) {
                return
            }

            ApplicationManager.getApplication().executeOnPooledThread {
                try {
                    val destFile = fileChooser!!.selectedFile
                    getInstance(project).configExternalizer.write(destFile)
                } catch (ex: Exception) {
                    ApplicationManager.getApplication().invokeLater {
                        Messages.showErrorDialog(
                            project,
                            LabelsFactory[LabelsFactory.MSG_EXPORT_FAILED, ex.toString()], FileSyncPlugin.PLUGIN_NAME
                        )
                    }
                    Logger.getInstance("FileSync").info("Error while exporting settings", ex)
                }
            }
        }
    }

    class ImportAction(private val configPanel: ConfigPanel) : AbstractAction(LabelsFactory[LabelsFactory.BN_IMPORT]) {
        private val fcDescriptor: FileChooserDescriptor = FileChooserDescriptor(true, false, false, false, false, false)
        private var selectedFile: VirtualFile? = null

        init {;
            fcDescriptor.withExtensionFilter("xml")
            fcDescriptor.title = LabelsFactory[LabelsFactory.LB_TITLE_IMPORT_SELECT]
            fcDescriptor.description = LabelsFactory[LabelsFactory.LB_DESC_IMPORT_SELECT]
        }

        override fun actionPerformed(e: ActionEvent) {
            val project = configPanel.currentProject
            if (selectedFile == null) {
                selectedFile = project.guessProjectDir()
            }
            val virtualFiles = FileChooser.chooseFiles(fcDescriptor, project, selectedFile)
            if (virtualFiles.size == 0) {
                return
            }
            selectedFile = virtualFiles[0]
            val plugin = getInstance(project)
            try {
                plugin.configExternalizer.read(
                    File(selectedFile!!.path)
                )
            } catch (ex: Exception) {
                Messages.showErrorDialog(
                    project,
                    LabelsFactory[LabelsFactory.MSG_IMPORT_FAILED, ex.toString()], FileSyncPlugin.PLUGIN_NAME
                )
                Logger.getInstance("FileSync").info("Error while importing settings", ex)
                return
            }
            configPanel.reset(plugin.stateComponent.state)
        }
    }
}