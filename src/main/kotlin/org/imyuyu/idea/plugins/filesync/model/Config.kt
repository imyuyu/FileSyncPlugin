package org.imyuyu.idea.plugins.filesync.model

import org.imyuyu.idea.plugins.filesync.utils.LabelsFactory
import java.io.Serializable
import java.util.*

/**
 * Store plugin settings
 */
class Config : Serializable {
    @JvmField
    var generalOptions: GeneralOptions
    @JvmField
    var logOptions: LogOptions
    private var targetMappings: MutableList<TargetMappings>

    @Transient
    private val listeners: MutableList<ConfigListener>

    init {
        targetMappings = ArrayList()
        addDefaultTarget()
        generalOptions = GeneralOptions()
        logOptions = LogOptions()
        listeners = LinkedList()
    }

    fun getTargetMappings(): MutableList<TargetMappings> {
        return targetMappings
    }

    fun setTargetMappings(targetMappings: MutableList<TargetMappings>) {
        this.targetMappings = targetMappings
    }

    fun addConfigListener(l: ConfigListener) {
        listeners.add(l)
    }

    fun removeConfigListener(l: ConfigListener) {
        listeners.remove(l)
    }

    fun fireConfigChanged() {
        val listenersCopy = LinkedList(listeners)
        for (listener in listenersCopy) {
            listener.configChanged(this)
        }
    }

    fun addDefaultTarget(): TargetMappings {
        val result = TargetMappings()
        result.name = LabelsFactory[LabelsFactory.TITLE_DEFAULT_TARGET_NAME]
        targetMappings.add(result)
        return result
    }

    fun hasActiveTarget(): Boolean {
        for (targetMapping in targetMappings) {
            if (targetMapping.isActive) {
                return true
            }
        }
        return false
    }

    class GeneralOptions : Serializable {
        var isStoreRelativePaths = true
        var isSaveBeforeCopy = true
        var isCopyOnSave = false
        var isCreateMissingDirs = true
        var isSimulationMode = false
        var isAllowConcurrentRuns = false

    }

    class LogOptions : Serializable {
        var isLogSrcPaths = false
        var isLogExludedPaths = true
        var isLogIdenticalPaths = true
        var isClearBeforeSynchro = false
        var isAutoPopup = true
        @JvmField
        var logFontFamily: String
        @JvmField
        var logFontSize: Int

        init {
            logFontFamily = DEFAULT_LOG_FONT_FAMILY
            logFontSize = DEFAULT_LOG_FONT_SIZE
        }
    }

    companion object {
        private const val DEFAULT_LOG_FONT_FAMILY = "Monospaced"
        private const val DEFAULT_LOG_FONT_SIZE = 12
    }
}
