package org.imyuyu.idea.plugins.filesync.ui.dialogs

import com.intellij.openapi.ui.Messages
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import org.imyuyu.idea.plugins.filesync.FileSyncPlugin
import org.imyuyu.idea.plugins.filesync.utils.ConfigPathsManager
import org.imyuyu.idea.plugins.filesync.utils.LabelsFactory
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel

/**
 * Intended to get an excluded path
 */
abstract class ExcludedPathDialog(
    pathManager: ConfigPathsManager, defaultValue: Any?,
    title: String?, label: String
) : AbstractPathDialog(pathManager, defaultValue) {
    private var tfPath: TextFieldWithBrowseButton? = null
    private val label: String

    init {
        setTitle(title)
        this.label = label
    }

    override fun init() {
        tfPath = createTextField()
        super.init()
    }

    public override fun updateDialogFromValue() {
        tfPath!!.text = if (value == null) "" else value.toString()
    }

    override fun updateValueFromDialog() {
        value = tfPath!!.text
    }

    override fun checkDialogValues(): Boolean {
        if ("" == tfPath!!.text) {
            Messages.showMessageDialog(
                LabelsFactory[LabelsFactory.MSG_INVALID_PATH],
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
        val lb = JLabel(label)
        panel.add(lb, c)
        c.gridy++
        c.weightx = 1.0
        c.fill = GridBagConstraints.HORIZONTAL
        c.insets = Insets(0, 5, 0, 5)
        panel.add(tfPath, c)

        /*c.gridx++;
    c.weightx = 0.0;
    c.fill = c.NONE;
    c.insets = new Insets(0, 0, 0, 5);
    JButton bnBrowse = createBrowseButton(tfPath, true);
    panel.add(bnBrowse, c);*/return panel
    }

    override fun getPreferredFocusedComponent(): JComponent? {
        return tfPath
    }

    val path: String
        get() = tfPath!!.text
}