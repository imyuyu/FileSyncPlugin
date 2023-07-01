package org.imyuyu.idea.plugins.filesync.ui.config.panes

import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBCheckBox
import org.imyuyu.idea.plugins.filesync.model.Config
import org.imyuyu.idea.plugins.filesync.utils.ConfigPathsManager
import org.imyuyu.idea.plugins.filesync.utils.LabelsFactory
import org.imyuyu.idea.plugins.filesync.utils.PathsUtils.getRelativePath
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.JCheckBox
import javax.swing.JPanel

/**/
class GeneralPane(private val project: Project, private val pathManager: ConfigPathsManager) : JPanel(), IConfigPane {
    private var ckStoreRelativePaths: JCheckBox? = null
    private var ckSaveBeforeCopy: JCheckBox? = null
    private var ckCopyOnSave: JCheckBox? = null
    private var ckCreateMissingDirs: JCheckBox? = null
    private var ckSimulationMode: JCheckBox? = null
    private var ckAllowConcurrentRuns: JCheckBox? = null
    override val title: String
        get() = LabelsFactory[LabelsFactory.PANEL_GENERAL]

    override fun isModified(config: Config): Boolean {
        val generalOptions = config.generalOptions
        return !(generalOptions.isStoreRelativePaths == ckStoreRelativePaths!!.isSelected && generalOptions.isSaveBeforeCopy == ckSaveBeforeCopy!!.isSelected && generalOptions.isCopyOnSave == ckCopyOnSave!!.isSelected && generalOptions.isCreateMissingDirs == ckCreateMissingDirs!!.isSelected && generalOptions.isAllowConcurrentRuns == ckAllowConcurrentRuns!!.isSelected && generalOptions.isSimulationMode == ckSimulationMode!!.isSelected)
    }

    override fun reset(config: Config) {
        // Prevent both options to be true
        if (config.generalOptions.isSaveBeforeCopy) {
            config.generalOptions.isCopyOnSave = false
        }
        val generalOptions = config.generalOptions
        ckStoreRelativePaths!!.isSelected = generalOptions.isStoreRelativePaths
        ckSaveBeforeCopy!!.isSelected = generalOptions.isSaveBeforeCopy
        ckCopyOnSave!!.isSelected = generalOptions.isCopyOnSave
        ckCreateMissingDirs!!.isSelected = generalOptions.isCreateMissingDirs
        ckSimulationMode!!.isSelected = generalOptions.isSimulationMode
        ckAllowConcurrentRuns!!.isSelected = generalOptions.isAllowConcurrentRuns
        ckCopyOnSave!!.isEnabled = !ckSaveBeforeCopy!!.isSelected
        ckSaveBeforeCopy!!.isEnabled = !ckCopyOnSave!!.isSelected
    }

    override fun apply(config: Config) {
        val generalOptions = config.generalOptions
        if (generalOptions.isStoreRelativePaths != ckStoreRelativePaths!!.isSelected) {
            for (targetMappings in config.getTargetMappings()) {
                for (mapping in targetMappings.synchroMappings) {
                    var path = if (ckStoreRelativePaths!!.isSelected) getRelativePath(
                        project,
                        mapping!!.srcPath!!
                    ) else pathManager.expandPath(
                        mapping!!.srcPath, true
                    )
                    mapping.srcPath = path
                    path = if (ckStoreRelativePaths!!.isSelected) getRelativePath(
                        project,
                        mapping.destPath!!
                    ) else pathManager.expandPath(
                        mapping.destPath, true
                    )
                    mapping.destPath = path
                }
            }
        }
        generalOptions.isStoreRelativePaths = ckStoreRelativePaths!!.isSelected
        generalOptions.isSaveBeforeCopy = ckSaveBeforeCopy!!.isSelected
        generalOptions.isCopyOnSave = ckCopyOnSave!!.isSelected
        generalOptions.isCreateMissingDirs = ckCreateMissingDirs!!.isSelected
        generalOptions.isSimulationMode = ckSimulationMode!!.isSelected
        generalOptions.isAllowConcurrentRuns = ckAllowConcurrentRuns!!.isSelected
    }

    override fun buildUI(pathsManager: ConfigPathsManager?) {
        ckStoreRelativePaths = JBCheckBox(LabelsFactory[LabelsFactory.LB_STORE_RELATIVE_PATHS])
        ckSaveBeforeCopy = JBCheckBox(LabelsFactory[LabelsFactory.LB_SAVE_BEFORE_COPY])
        ckCopyOnSave = JBCheckBox(LabelsFactory[LabelsFactory.LB_COPY_ON_SAVE])
        ckCreateMissingDirs = JBCheckBox(LabelsFactory[LabelsFactory.LB_CREATE_MISSING_DIRS])
        ckSimulationMode = JBCheckBox(LabelsFactory[LabelsFactory.LB_SIMULATION_MODE])
        ckAllowConcurrentRuns = JBCheckBox(LabelsFactory[LabelsFactory.LB_ALLOW_CONCURRENT_RUNS])
        layout = GridBagLayout()
        val c = GridBagConstraints()

        // Save before copy
        c.gridx = 0
        c.gridy = 0
        c.anchor = GridBagConstraints.NORTHWEST
        c.weightx = 1.0
        add(ckSimulationMode, c)

        // Store relative paths
        c.gridy++
        add(ckStoreRelativePaths, c)

        // Save before synchronize
        c.gridy++
        add(ckSaveBeforeCopy, c)

        // Synchronize on save
        c.gridy++
        add(ckCopyOnSave, c)

        // Create missing dirs
        c.gridy++
        add(ckCreateMissingDirs, c)

        // Create missing dirs
        c.gridy++
        c.weighty = 1.0
        add(ckAllowConcurrentRuns, c)
        handleSaveCopyOptionsConflict()
    }

    private fun handleSaveCopyOptionsConflict() {
        ckCopyOnSave!!.addActionListener { ckSaveBeforeCopy!!.isEnabled = !ckCopyOnSave!!.isSelected }
        ckSaveBeforeCopy!!.addActionListener { ckCopyOnSave!!.isEnabled = !ckSaveBeforeCopy!!.isSelected }
    }
}
