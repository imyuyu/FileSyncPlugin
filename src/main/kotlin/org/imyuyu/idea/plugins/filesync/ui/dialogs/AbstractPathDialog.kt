package org.imyuyu.idea.plugins.filesync.ui.dialogs

import com.intellij.openapi.Disposable
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.vfs.VirtualFile
import org.imyuyu.idea.plugins.filesync.utils.ConfigPathsManager
import org.imyuyu.idea.plugins.filesync.utils.PathsUtils.getRelativePath
import org.imyuyu.idea.plugins.filesync.utils.PathsUtils.getVirtualFile
import org.imyuyu.idea.plugins.filesync.utils.PathsUtils.toModelPath
import java.awt.Component
import java.awt.Dimension
import java.awt.event.ActionEvent
import java.io.File
import javax.swing.JButton

/**
 * Base dialog used to edit a file path
 */
abstract class AbstractPathDialog(protected var pathManager: ConfigPathsManager, var value: Any?) : DialogWrapper(true),
    Disposable {
    protected var fcDescriptor: FileChooserDescriptor

    init {
        fcDescriptor = FileChooserDescriptor(true, true, true, true, false, false)
        this.init()
        this.updateDialogFromValue()
    }

    protected fun createTextField(): TextFieldWithBrowseButton {
        val browseButton = TextFieldWithBrowseButton(null, this)
        browseButton.addBrowseFolderListener(pathManager.plugin.project, fcDescriptor);
        browseButton.textField.columns = TEXT_SIZE
        browseButton.textField.addActionListener { doOKAction() }
        return browseButton
    }

    protected fun formatInputPath(path: String): String {
        var f = File(path)
        if (!f.isAbsolute) {
            f = File(pathManager.projectDefaultRoot!!.path, path)
        }
        var result = toModelPath(f.absolutePath)
        if (pathManager.plugin.config.generalOptions.isStoreRelativePaths) {
            result = getRelativePath(pathManager.plugin.project, result)
        }
        val c = path[path.length - 1]
        if (c == '/' || c == '\\') {
            result += '/'
        }
        return result
    }

    protected fun createBrowseButton(textField: TextFieldWithBrowseButton, useAntPattern: Boolean): JButton {
        val button = JButton("...")
        button.preferredSize = Dimension(
            20,
            textField.preferredSize.height
        )
        button.addActionListener { e ->
            var toSelect: VirtualFile? = null
            if (textField.text.length > 0) {
                toSelect = getVirtualFile(textField.text.replace('\\', '/'))
            }
            if (toSelect == null) {
                toSelect = pathManager.projectDefaultRoot
            }
            val virtualFiles = FileChooser.chooseFiles(
                fcDescriptor, e.source as Component,
                pathManager.plugin.project, toSelect
            )
            if (virtualFiles.size > 0) {
                textField.text = getPathForTextField(virtualFiles[0], useAntPattern)
            }
        }
        return button
    }

    private fun getPathForTextField(virtualFile: VirtualFile, useAntPattern: Boolean): String {
        var path = virtualFile.path
        if (useAntPattern) {
            path = toModelPath(path)
            if (virtualFile.isDirectory) {
                path += "/*"
            }
            return path
        }
        return pathManager.toPresentablePath(path)
    }

    override fun doOKAction() {
        if (checkDialogValues()) {
            updateValueFromDialog()
            super.doOKAction()
        }
    }

    protected abstract fun updateDialogFromValue()
    protected abstract fun updateValueFromDialog()
    protected abstract fun checkDialogValues(): Boolean
    override fun dispose() {
        super.dispose()
    }

    companion object {
        private const val TEXT_SIZE = 40
    }
}
