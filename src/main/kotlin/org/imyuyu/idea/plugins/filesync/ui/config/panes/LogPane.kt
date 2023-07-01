package org.imyuyu.idea.plugins.filesync.ui.config.panes

import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBPanel
import com.intellij.util.ui.JBInsets
import org.imyuyu.idea.plugins.filesync.model.Config
import org.imyuyu.idea.plugins.filesync.utils.ConfigPathsManager
import org.imyuyu.idea.plugins.filesync.utils.LabelsFactory
import java.awt.GraphicsEnvironment
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.*

/**/
class LogPane : JPanel(), IConfigPane {
    private var ckClearBeforeSynchro: JCheckBox? = null
    private var ckAutoPopup: JCheckBox? = null
    private var cbFontFamily: JComboBox<*>? = null
    private var cbFontSize: JComboBox<*>? = null
    private var ckLogSrcPaths: JCheckBox? = null
    private var ckLogExludedPaths: JCheckBox? = null
    private var ckLogIdenticalPaths: JCheckBox? = null
    override val title: String
        get() = LabelsFactory[LabelsFactory.PANEL_LOGS]

    override fun isModified(config: Config): Boolean {
        val logOptions = config.logOptions
        return !(logOptions.logFontFamily == cbFontFamily!!.selectedItem && logOptions.logFontSize.toString() == cbFontSize!!.selectedItem && logOptions.isLogExludedPaths == ckLogExludedPaths!!.isSelected && logOptions.isLogSrcPaths == ckLogSrcPaths!!.isSelected && logOptions.isLogIdenticalPaths == ckLogIdenticalPaths!!.isSelected && logOptions.isClearBeforeSynchro == ckClearBeforeSynchro!!.isSelected && logOptions.isAutoPopup == ckAutoPopup!!.isSelected)
    }

    override fun reset(config: Config) {
        val logOptions = config.logOptions
        cbFontFamily!!.selectedItem = logOptions.logFontFamily
        cbFontSize!!.selectedItem = logOptions.logFontSize.toString()
        ckLogExludedPaths!!.isSelected = logOptions.isLogExludedPaths
        ckLogSrcPaths!!.isSelected = logOptions.isLogSrcPaths
        ckLogIdenticalPaths!!.isSelected = logOptions.isLogIdenticalPaths
        ckClearBeforeSynchro!!.isSelected = logOptions.isClearBeforeSynchro
        ckAutoPopup!!.isSelected = logOptions.isAutoPopup
    }

    override fun apply(config: Config) {
        val logOptions = config.logOptions
        logOptions.logFontFamily = cbFontFamily!!.selectedItem.toString()
        logOptions.logFontSize = cbFontSize!!.selectedItem.toString().toInt()
        logOptions.isLogExludedPaths = ckLogExludedPaths!!.isSelected
        logOptions.isLogSrcPaths = ckLogSrcPaths!!.isSelected
        logOptions.isLogIdenticalPaths = ckLogIdenticalPaths!!.isSelected
        logOptions.isClearBeforeSynchro = ckClearBeforeSynchro!!.isSelected
        logOptions.isAutoPopup = ckAutoPopup!!.isSelected
    }

    override fun buildUI(pathsManager: ConfigPathsManager?) {
        ckClearBeforeSynchro = JBCheckBox(LabelsFactory[LabelsFactory.LB_CLEAR_BEFORE_SYNCHRO])
        ckAutoPopup = JBCheckBox(LabelsFactory[LabelsFactory.LB_AUTO_POPUP_LOGS])
        layout = GridBagLayout()
        val c = GridBagConstraints()

        // Clear before synchro
        c.gridx = 0
        c.gridy = 0
        c.weightx = 1.0
        c.anchor = GridBagConstraints.NORTHWEST
        add(ckClearBeforeSynchro, c)

        // Auto popup
        c.gridy++
        add(ckAutoPopup, c)

        // Font panel
        c.gridy++
        add(createFontPanel(), c)

        // Show paths panel
        c.gridy++
        c.weighty = 1.0
        add(createShowPathsPanel(), c)
    }

    private fun createFontPanel(): JPanel {
        cbFontFamily = ComboBox(FONT_NAMES)
        cbFontSize = ComboBox(FONT_SIZES)
        val result: JPanel = JBPanel<JBPanel<*>>(GridBagLayout())
        result.border = BorderFactory.createTitledBorder(
            LabelsFactory[LabelsFactory.PANEL_FONT]
        )
        //result.setBorder(IdeBorderFactory.createTitledBorder(LabelsFactory.get(LabelsFactory.PANEL_FONT)));
        val c = GridBagConstraints()

        // Font family
        c.gridx = 0
        c.gridy = 0
        c.insets = JBInsets(5, 5, 5, 10)
        c.anchor = GridBagConstraints.NORTHWEST
        result.add(JLabel(LabelsFactory[LabelsFactory.LB_FONT_FAMILY]), c)
        c.gridx++
        c.insets = JBInsets(5, 0, 5, 20)
        result.add(cbFontFamily, c)

        // Font size
        c.gridx++
        c.insets = JBInsets(5, 0, 5, 10)
        result.add(JLabel(LabelsFactory[LabelsFactory.LB_FONT_SIZE]), c)
        c.gridx++
        c.insets = JBInsets(5, 0, 5, 5)
        c.weightx = 1.0
        result.add(cbFontSize, c)
        return result
    }

    private fun createShowPathsPanel(): JPanel {
        ckLogSrcPaths = JBCheckBox(LabelsFactory[LabelsFactory.LB_SHOW_SOURCE_PATHS])
        ckLogExludedPaths = JBCheckBox(LabelsFactory[LabelsFactory.LB_SHOW_EXCLUDED_PATHS])
        ckLogIdenticalPaths = JBCheckBox(LabelsFactory[LabelsFactory.LB_SHOW_IDENTICAL_PATHS])
        val result: JPanel = JBPanel<JBPanel<*>>(GridBagLayout())
        result.border = BorderFactory.createTitledBorder(
            LabelsFactory[LabelsFactory.PANEL_SHOW_PATHS]
        )
        val c = GridBagConstraints()

        // Source paths
        c.gridy++
        c.anchor = GridBagConstraints.NORTHWEST
        result.add(ckLogSrcPaths, c)

        // Excluded paths
        c.gridy++
        result.add(ckLogExludedPaths, c)

        // Identical paths
        c.gridy++
        c.weighty = 1.0
        result.add(ckLogIdenticalPaths, c)
        return result
    }

    companion object {
        private val FONT_SIZES = arrayOf(
            "6", "7", "8", "9", "10", "11", "12", "13", "14",
            "16", "18", "20", "24", "28", "32", "48", "64"
        )
        private val FONT_NAMES = GraphicsEnvironment
            .getLocalGraphicsEnvironment().availableFontFamilyNames
    }
}
