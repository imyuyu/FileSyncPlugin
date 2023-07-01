package org.imyuyu.idea.plugins.filesync.ui.dialogs

import com.intellij.openapi.ui.Messages
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import org.imyuyu.idea.plugins.filesync.FileSyncPlugin
import org.imyuyu.idea.plugins.filesync.model.SynchroMapping
import org.imyuyu.idea.plugins.filesync.utils.ConfigPathsManager
import org.imyuyu.idea.plugins.filesync.utils.LabelsFactory
import org.imyuyu.idea.plugins.filesync.utils.PathsUtils.isAncestor
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import javax.swing.JCheckBox
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel

/**
 * Intended to get an included path
 */
class SynchroMappingDialog(pathManager: ConfigPathsManager, defaultValue: Any?) :
    AbstractPathDialog(pathManager, defaultValue) {
    private var tfSrcPath: TextFieldWithBrowseButton? = null
    private var tfDestPath: TextFieldWithBrowseButton? = null
    private var ckDeleteObsolete: JCheckBox? = null
    override fun init() {
        title = LabelsFactory[LabelsFactory.FRAME_INCLUDED_PATH]
        tfSrcPath = createTextField()
        tfDestPath = createTextField()
        ckDeleteObsolete = JCheckBox(LabelsFactory[LabelsFactory.LB_DELETE_OBSOLETE])
        super.init()
    }

    override fun updateDialogFromValue() {
        if (value == null) {
            tfSrcPath!!.text = ""
            tfDestPath!!.text = ""
            ckDeleteObsolete!!.isSelected = true
        } else {
            val p = value as SynchroMapping
            tfSrcPath!!.setText(pathManager.expandPath(pathManager.toPresentablePath(p.srcPath!!), false))
            tfDestPath!!.setText(pathManager.expandPath(pathManager.toPresentablePath(p.destPath!!), false))
            ckDeleteObsolete!!.isSelected = p.isDeleteObsoleteFiles
        }
    }

    override fun updateValueFromDialog() {
        if (value == null) value = SynchroMapping()
        val p = value as SynchroMapping
        p.srcPath = formatInputPath(tfSrcPath!!.text)
        p.destPath = formatInputPath(tfDestPath!!.text)
        p.isDeleteObsoleteFiles = ckDeleteObsolete!!.isSelected
    }

    override fun checkDialogValues(): Boolean {
        if ("" == tfSrcPath!!.text) {
            Messages.showMessageDialog(
                LabelsFactory[LabelsFactory.MSG_INVALID_SRC_PATH],
                FileSyncPlugin.PLUGIN_NAME, Messages.getErrorIcon()
            )
            return false
        }
        if ("" == tfDestPath!!.text) {
            Messages.showMessageDialog(
                LabelsFactory[LabelsFactory.MSG_INVALID_DEST_PATH],
                FileSyncPlugin.PLUGIN_NAME, Messages.getErrorIcon()
            )
            return false
        }
        if (isAncestor(tfDestPath!!.text, tfSrcPath!!.text)) {
            Messages.showMessageDialog(
                LabelsFactory[LabelsFactory.MSG_SRC_IN_DEST_PATH],
                FileSyncPlugin.PLUGIN_NAME, Messages.getErrorIcon()
            )
            return false
        }
        return true
    }

    override fun createCenterPanel(): JComponent? {
        val panel = JPanel(GridBagLayout())
        val c = GridBagConstraints()
        c.gridx = 0
        c.gridy = 0
        c.anchor = GridBagConstraints.SOUTHWEST
        c.insets = Insets(5, 5, 0, 0)
        var label = JLabel(LabelsFactory[LabelsFactory.LB_SRC_PATH])
        panel.add(label, c)
        c.gridy++
        c.weightx = 1.0
        c.fill = GridBagConstraints.HORIZONTAL
        c.insets = Insets(0, 5, 0, 5)
        panel.add(tfSrcPath, c)

        /*c.gridx++;
    c.weightx = 0.0;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(0, 0, 0, 5);
    JButton bnBrowse = createBrowseButton(tfSrcPath, false);
    panel.add(bnBrowse, c);*/c.gridx = 0
        c.gridy++
        c.insets = Insets(8, 5, 0, 0)
        label = JLabel(LabelsFactory[LabelsFactory.LB_DEST_PATH])
        panel.add(label, c)
        c.gridy++
        c.weightx = 1.0
        c.fill = GridBagConstraints.HORIZONTAL
        c.insets = Insets(0, 5, 0, 5)
        panel.add(tfDestPath, c)

        /*c.gridx++;
    c.weightx = 0.0;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(0, 0, 0, 5);
    bnBrowse = createBrowseButton(tfDestPath, false);
    panel.add(bnBrowse, c);*/c.gridx = 0
        c.gridy++
        c.insets = Insets(8, 5, 0, 0)
        panel.add(ckDeleteObsolete, c)
        return panel
    }

    /**
     * @return component which should be focused when the the dialog appears on the screen.
     */
    override fun getPreferredFocusedComponent(): JComponent? {
        return tfSrcPath
    }

    override fun isOK(): Boolean {
        return tfSrcPath!!.text.length > 2
    }

    val srcPath: String
        get() = tfSrcPath!!.text
    val destPath: String
        get() = tfDestPath!!.text
}