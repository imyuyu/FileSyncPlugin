package org.imyuyu.idea.plugins.filesync.ui

import com.intellij.openapi.vfs.VirtualFile
import org.imyuyu.idea.plugins.filesync.model.Config
import org.imyuyu.idea.plugins.filesync.model.ConfigListener
import org.imyuyu.idea.plugins.filesync.model.SyncronizingStatsInfo
import org.imyuyu.idea.plugins.filesync.synchronizing.SynchronizerThread
import org.imyuyu.idea.plugins.filesync.synchronizing.SynchronizerThreadListener
import org.imyuyu.idea.plugins.filesync.utils.LabelsFactory
import java.awt.Color
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.swing.JScrollPane
import javax.swing.JTextPane
import javax.swing.SwingUtilities
import javax.swing.text.BadLocationException
import javax.swing.text.StyleConstants
import javax.swing.text.StyleContext

/**
 * Logs component. A thread console is linked to one thread synchronizer
 */
class ThreadConsole(
  private val consolePane: ThreadConsolePane, private val config: Config,
  val thread: SynchronizerThread, var title: String?
) : JScrollPane(), ConfigListener, SynchronizerThreadListener {
    private var mainConsole: ThreadConsole? = null
    private val textPane: JTextPane
    var isCleared = false
        private set
    private var simulationMode: Boolean

    init {
        simulationMode = config.generalOptions.isSimulationMode
        thread.setListener(this)
        textPane = JTextPane()
        setViewportView(textPane)
        textPane.isEditable = false
        updateFont()
        clear()
    }

    fun isMainConsole(): Boolean {
        return mainConsole == null
    }

    fun getMainConsole(): ThreadConsole? {
        return mainConsole
    }

    fun setMainConsole(mainConsole: ThreadConsole?) {
        this.mainConsole = mainConsole
    }

    fun updateFont() {
        val logOptions = config.logOptions
        val baseStyle = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE)
        val defautStyle = textPane.addStyle(DEFAULT_STYLE, baseStyle)
        StyleConstants.setFontFamily(defautStyle, logOptions.logFontFamily)
        StyleConstants.setFontSize(defautStyle, logOptions.logFontSize)
        val simulationStyle = textPane.addStyle(SIMULATION_STYLE, baseStyle)
        StyleConstants.setFontFamily(simulationStyle, logOptions.logFontFamily)
        StyleConstants.setFontSize(simulationStyle, logOptions.logFontSize)
        StyleConstants.setItalic(simulationStyle, true)
        val headerStyle = textPane.addStyle(HEADER_STYLE, defautStyle)
        StyleConstants.setBold(headerStyle, true)
        val errorStyle = textPane.addStyle(ERROR_STYLE, defautStyle)
        StyleConstants.setForeground(errorStyle, Color.red)
    }

    fun updateTitle() {
        var tmpTitle = thread.targetMappings.name
        if (mainConsole != null) tmpTitle += consoleNameSuffix
        title = tmpTitle
        consolePane.updateTitle(this)
    }

    private val consoleNameSuffix: String
        private get() {
            val i = title!!.lastIndexOf(" (")
            return if (i == -1) "" else title!!.substring(i)
        }

    fun hasSameTargetMappings(console: ThreadConsole): Boolean {
        return thread.targetMappings == console.thread.targetMappings
    }

    fun breakLogs() {
        append("")
    }

    fun clear() {
        try {
            textPane.document.remove(0, textPane.document.length)
        } catch (e: BadLocationException) {
            e.printStackTrace()
        }
        append(
            LabelsFactory[LabelsFactory.MSG_SYMBOLS] + " : "
                    + INFO_COPY_NEW + ":"
                    + LabelsFactory[LabelsFactory.MSG_SYMBOL_NEW]
                    + ", "
                    + INFO_COPY_REPLACE + ":"
                    + LabelsFactory[LabelsFactory.MSG_SYMBOL_REPLACE]
                    + ", "
                    + INFO_COPY_EQUAL + ":"
                    + LabelsFactory[LabelsFactory.MSG_SYMBOL_EQUAL]
                    + ", "
                    + INFO_COPY_DELETED + ":"
                    + LabelsFactory[LabelsFactory.MSG_SYMBOL_DELETED]
                    + ", "
                    + INFO_COPY_NO_CLASS + ":"
                    + LabelsFactory[LabelsFactory.MSG_SYMBOL_NO_CLASS]
                    + ", "
                    + INFO_COPY_EXCLUDED + ":"
                    + LabelsFactory[LabelsFactory.MSG_SYMBOL_EXCLUDED]
                    + "",
            HEADER_STYLE
        )
        breakLogs()
        if (simulationMode) append(
            " - " + LabelsFactory[LabelsFactory.MSG_SIMULATION_ACTIVED]
                    + " -\n", DEFAULT_STYLE
        )
        isCleared = true
    }

    private fun append(s: String, styleName: String = if (simulationMode) SIMULATION_STYLE else DEFAULT_STYLE) {
        SwingUtilities.invokeLater {
            try {
                val doc = textPane.document
                val start = doc.length
                // doc.insertString(start, s + "\n", textPane.getStyle(styleName));
                doc.insertString(start, "*$s\n", textPane.getStyle(styleName))
                doc.remove(start, 1)

                // Add a temporary '*' and then remove it because of a bug of the
                // StyledEditorKit, see the test class
            } catch (ignored: BadLocationException) {
            }
            scrollToEnd()
        }
    }

    private fun scrollToEnd() {
        textPane.caretPosition = textPane.document.length
    }

    override fun configChanged(config: Config?) {
        updateFont()
        if (simulationMode != config!!.generalOptions.isSimulationMode) {
            append(
                " - "
                        + LabelsFactory[if (simulationMode) LabelsFactory.MSG_SIMULATION_DEACTIVED else LabelsFactory.MSG_SIMULATION_ACTIVED]
                        + " -\n", DEFAULT_STYLE
            )
        }
        simulationMode = config.generalOptions.isSimulationMode
    }

    override fun threadStarted(thread: SynchronizerThread?, runnable: Runnable?, files: Array<VirtualFile>) {
        consolePane.updateTitle(this)
        if (config.logOptions.isClearBeforeSynchro) clear()
        if (config.logOptions.isAutoPopup) consolePane.doPopup()
    }

    override fun threadResumed(thread: SynchronizerThread?) {
        consolePane.updateTitle(this)
    }

    override fun threadStopped(
        thread: SynchronizerThread?,
        statsInfo: SyncronizingStatsInfo?
    ) {
        consolePane.updateTitle(this)
        append(" - " + LabelsFactory[LabelsFactory.MSG_COPY_STOPPED] + " - ")
        breakLogs()
        isCleared = false
    }

    override fun threadInterrupted(thread: SynchronizerThread?) {
        consolePane.updateTitle(this)
        append(" - " + LabelsFactory[LabelsFactory.MSG_COPY_INTERRUPTED] + " - ")
        isCleared = false
    }

    override fun threadFinished(
        copierThread: SynchronizerThread?,
        statsInfo: SyncronizingStatsInfo?
    ) {
        var count = (statsInfo!!.successCount
                + statsInfo.ignoredCount
                + statsInfo.failuresCount)
        if (config.logOptions.isLogExludedPaths) count += statsInfo.excludedCount
        if (count == 0) {
            append(LabelsFactory[LabelsFactory.MSG_NO_FILE_COPIED])
        } else {
            if (statsInfo.successCount > 0) {
                val s =
                    if (statsInfo.successCount == 1) LabelsFactory[LabelsFactory.MSG_NB_FILE_COPIED] else LabelsFactory[LabelsFactory.MSG_NB_FILES_COPIED]
                append(statsInfo.successCount.toString() + " " + s)
            }
            if (statsInfo.deletedCount > 0) {
                val s =
                    if (statsInfo.deletedCount == 1) LabelsFactory[LabelsFactory.MSG_NB_FILE_DELETED] else LabelsFactory[LabelsFactory.MSG_NB_FILES_DELETED]
                append(statsInfo.deletedCount.toString() + " " + s)
            }
            count = statsInfo.ignoredCount
            if (config.logOptions.isLogExludedPaths) count += statsInfo.excludedCount
            if (count > 0) {
                val s =
                    if (count == 1) LabelsFactory[LabelsFactory.MSG_NB_FILE_IGNORED] else LabelsFactory[LabelsFactory.MSG_NB_FILES_IGNORED]
                append("$count $s")
            }
            if (statsInfo.failuresCount > 0) {
                val s =
                    if (statsInfo.failuresCount == 1) LabelsFactory[LabelsFactory.MSG_NB_FAILURE] else LabelsFactory[LabelsFactory.MSG_NB_FAILURES]
                append(statsInfo.failuresCount.toString() + " " + s, ERROR_STYLE)
            }
        }
        consolePane.updateTitle(this)
        breakLogs()
        isCleared = false
    }

    override fun fileCopying(
        thread: SynchronizerThread?, src: String?, dest: String?,
        copyType: Int
    ) {
        val pathsManager = thread!!.plugin.pathManager
        val tmpSrc = pathsManager.toPresentablePath(src!!)
        var tmpDest = dest
        if (tmpDest != null) {
            tmpDest = pathsManager.toPresentablePath(dest!!)
        }
        val time = TIME_FORMATTER.format(Date())
        val copyTypeInfo = if (copyType == -1) "" else COPY_INFOS[copyType]
        if (tmpDest == null) {
            if (config.logOptions.isLogExludedPaths) {
                append(
                    time + " " + copyTypeInfo + " "
                            + "(" + LabelsFactory[LabelsFactory.MSG_FROM]
                            + " " + tmpSrc + ")"
                )
            }
        } else if (copyType != SynchronizerThread.TYPE_COPY_IDENTICAL
            || config.logOptions.isLogIdenticalPaths
        ) {
            append("$time $copyTypeInfo $tmpDest")
            if (config.logOptions.isLogSrcPaths) {
                append(
                    "[ " + LabelsFactory[LabelsFactory.MSG_FROM]
                            + " " + tmpSrc + " ]"
                )
            }
        }
        isCleared = false
    }

    override fun fileDeleting(thread: SynchronizerThread?, path: String?) {
        var path = path
        val time = TIME_FORMATTER.format(Date())
        path = thread!!.plugin.pathManager.toPresentablePath(path!!)
        append(time + " " + INFO_COPY_DELETED + " " + path)
    }

    override fun copyFailed(thread: SynchronizerThread?, t: Throwable?) {
        val msg = if (t!!.message == null) t.toString() else t.message!!
        append(
            LabelsFactory[LabelsFactory.MSG_CANT_COPY_FILE] + " : " + msg,
            ERROR_STYLE
        )
        isCleared = false
    }

    override fun dirDeletionFailed(
        syncronizerThread: SynchronizerThread?,
        path: String?
    ) {
        append(LabelsFactory[LabelsFactory.MSG_CANT_DELETE_DIR], ERROR_STYLE)
        isCleared = false
    }

    override fun fileDeletionFailed(syncronizerThread: SynchronizerThread?) {
        append(LabelsFactory[LabelsFactory.MSG_CANT_DELETE_FILE], ERROR_STYLE)
        isCleared = false
    }

    companion object {
        // Types of symbols
        private const val INFO_COPY_REPLACE = "(/)"
        private const val INFO_COPY_NEW = "(+)"
        private const val INFO_COPY_DELETED = "(-)"
        private const val INFO_COPY_EQUAL = "(=)"
        private const val INFO_COPY_NO_CLASS = "(?)"
        private const val INFO_COPY_EXCLUDED = "(^)"
        private val COPY_INFOS = arrayOf(
            INFO_COPY_NEW, INFO_COPY_REPLACE, INFO_COPY_EQUAL, INFO_COPY_NO_CLASS,
            INFO_COPY_EXCLUDED
        )
        private val TIME_FORMATTER: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        private const val HEADER_STYLE = "HeaderStyle"
        private const val DEFAULT_STYLE = "DefaultStyle"
        private const val SIMULATION_STYLE = "SimulationStyle"
        private const val ERROR_STYLE = "ErrorStyle"
    }
}
