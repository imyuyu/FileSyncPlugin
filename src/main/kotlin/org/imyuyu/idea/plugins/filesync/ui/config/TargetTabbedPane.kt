package org.imyuyu.idea.plugins.filesync.ui.config

import org.imyuyu.idea.plugins.filesync.model.Config
import org.imyuyu.idea.plugins.filesync.model.TargetMappings
import org.imyuyu.idea.plugins.filesync.utils.ConfigPathsManager
import org.imyuyu.idea.plugins.filesync.utils.LabelsFactory
import org.imyuyu.idea.plugins.filesync.utils.LabelsFactory.get
import java.awt.*
import java.awt.event.*
import javax.swing.JDialog
import javax.swing.JPanel
import javax.swing.JTabbedPane
import javax.swing.JTextField

/**/
class TargetTabbedPane(private val pathsManager: ConfigPathsManager) : JTabbedPane() {
    private val nameBox: NameBox?

    init {
        if (!GraphicsEnvironment.isHeadless()) {
            nameBox = NameBox()
            addMouseListener(object : MouseAdapter() {
                public override fun mouseReleased(e: MouseEvent) {
                    if (e.getClickCount() == 2) {
                        val index: Int = indexAtLocation(e.getX(), e.getY())
                        if (index > -1) {
                            nameBox.startEdition(index)
                        }
                    }
                }
            })
        } else {
            nameBox = null;
        }
    }

    fun isModified(config: Config): Boolean {
        // Existings tabs
        for (i in 0 until getComponentCount()) {
            val tab: TargetTab = getComponentAt(i) as TargetTab
            if (((!config.getTargetMappings().contains(tab.targetMappings))
                        || (!(getTitleAt(i) == tab.targetMappings.name))
                        || (i != tab.initialPos)
                        || (tab.isModified(config)))
            ) {
                return true
            }
        }

        // Deleted tabs
        for (targetMappings: TargetMappings in config.getTargetMappings()) {
            if (!containsTab(targetMappings)) {
                return true
            }
        }
        return false
    }

    fun reset(config: Config) {
        removeAll()
        for (targetMappings: TargetMappings in config.getTargetMappings()) {
            add(
                targetMappings.name,
                TargetTab(targetMappings, this, pathsManager, getComponentCount())
            )
        }
    }

    fun apply(config: Config) {
        config.getTargetMappings().clear()
        for (i in 0 until getComponentCount()) {
            val tab: TargetTab = getComponentAt(i) as TargetTab
            tab.apply()
            tab.initialPos = i;
            tab.targetMappings.name = getTitleAt(i)
            config.getTargetMappings().add(tab.targetMappings)
        }
    }

    private fun containsTab(target: TargetMappings): Boolean {
        for (i in 0 until getComponentCount()) {
            if (((getComponentAt(i) as TargetTab).targetMappings == target)) {
                return true
            }
        }
        return false
    }

    private fun findNewName(): String {
        var found: Boolean = false
        var result: String = get(LabelsFactory.TITLE_DEFAULT_TARGET_NAME)
        for (i in 0 until getComponentCount()) {
            val tab: TargetTab = getComponentAt(i) as TargetTab
            if ((tab.targetMappings.name == result)) {
                found = true
                break
            }
        }
        if (!found) {
            return result
        }
        var inc: Int = 1
        result = (get(LabelsFactory.TITLE_DEFAULT_TARGET_NAME)
                + " (" + inc + ")")
        var i: Int = 0
        while (i < getComponentCount()) {
            val tab: TargetTab = getComponentAt(i) as TargetTab
            if ((tab.targetMappings.name == result)) {
                i = 0
                result = (get(LabelsFactory.TITLE_DEFAULT_TARGET_NAME)
                        + " (" + ++inc + ")")
            }
            i++
        }
        return result
    }

    fun addTarget() {
        val target: TargetMappings = TargetMappings()
        target.name = findNewName()
        val tab: TargetTab = TargetTab(
            target, this, pathsManager,
            getComponentCount()
        )
        add(target.name, tab)
        val index: Int = getComponentCount() - 1
        setSelectedComponent(tab)
        nameBox!!.startEdition(index)
    }

    public override fun getForegroundAt(index: Int): Color {
        val tab: TargetTab = getComponentAt(index) as TargetTab
        return if (tab.isSetAsActive) super.getForeground() else Color.gray
    }

    fun removeTarget() {
        remove(getSelectedComponent())
    }

    fun moveTargetToLeft() {
        moveTo(getSelectedIndex() - 1)
    }

    fun moveTargetToRight() {
        moveTo(getSelectedIndex() + 1)
    }

    private fun moveTo(dest: Int) {
        val tab: TargetTab = getSelectedComponent() as TargetTab
        // Title may be different from target's name
        val title: String = getTitleAt(getSelectedIndex())
        remove(tab)
        insertTab(title, null, tab, null, dest)
        setSelectedIndex(dest)
    }

    // Hum, textField is not editable on a popup menu...
    private inner class NameBox() : JDialog() {
        private val tfName: JTextField
        private var tabIndex: Int = 0

        init {
            setModal(true)
            setOpaque(true)
            setUndecorated(true)
            setResizable(false)
            tfName = JTextField()
            tfName.setHorizontalAlignment(CENTER)
            tfName.setFont(tfName.getFont().deriveFont(Font.BOLD))
            val panel: JPanel = JPanel()
            panel.add(tfName)
            setContentPane(panel)
            tfName.addKeyListener(object : KeyAdapter() {
                public override fun keyPressed(e: KeyEvent) {
                    if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                        endEdition(false)
                    }
                }
            })
            tfName.addActionListener(object : ActionListener {
                public override fun actionPerformed(e: ActionEvent) {
                    endEdition(true)
                }
            })
            tfName.addFocusListener(object : FocusAdapter() {
                public override fun focusLost(e: FocusEvent) {
                    endEdition(true)
                }
            })
        }

        public override fun getPreferredSize(): Dimension {
            return Dimension(
                getBoundsAt(tabIndex).width + 2,
                getBoundsAt(tabIndex).height + 2
            )
        }

        fun startEdition(tabIndex: Int) {
            this.tabIndex = tabIndex
            val tabbedPane: TargetTabbedPane = this@TargetTabbedPane
            tfName.setText(tabbedPane.getTitleAt(tabIndex))
            val onScreen: Point = tabbedPane.getLocationOnScreen().getLocation()
            setLocation(
                onScreen.getX().toInt() + tabbedPane.getBoundsAt(tabIndex).x,
                onScreen.getY().toInt() + tabbedPane.getBoundsAt(tabIndex).y
            )
            pack()
            setVisible(true)
            tfName.requestFocus()
            tfName.selectAll()
        }

        private fun endEdition(valid: Boolean) {
            if (!isVisible()) {
                return
            }
            if (valid) {
                setTitleAt(tabIndex, tfName.getText())
            }
            dispose()
        }
    }
}
