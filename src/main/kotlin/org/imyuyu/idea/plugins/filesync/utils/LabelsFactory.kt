package org.imyuyu.idea.plugins.filesync.utils

import com.intellij.DynamicBundle
import org.imyuyu.idea.plugins.filesync.FileSyncBundle
import java.text.MessageFormat
import java.util.*

/**
 * Factory for ui labels
 *
 *
 * Labels are stored in properties files located in **FileSyncPlugin**
 * package
 */
object LabelsFactory {

    const val BN_ADD_PATH = "BN_ADD_PATH"
    const val BN_ADD_TARGET = "BN_ADD_TARGET"
    const val BN_EDIT_TARGET_NAME = "BN_EDIT_TARGET_NAME"
    const val BN_EDIT_PATH = "BN_EDIT_PATH"
    const val BN_MOVE_TARGET_TO_LEFT = "BN_MOVE_TARGET_TO_LEFT"
    const val BN_MOVE_TARGET_TO_RIGHT = "BN_MOVE_TARGET_TO_RIGHT"
    const val BN_REMOVE_PATH = "BN_REMOVE_PATH"
    const val BN_REMOVE_TARGET = "BN_REMOVE_TARGET"
    const val BN_EXPORT = "BN_EXPORT"
    const val BN_IMPORT = "BN_IMPORT"
    const val COL_DEST_PATH = "COL_DEST_PATH"
    const val COL_EXCLUDED_COPY_PATH = "COL_EXCLUDED_COPY_PATH"
    const val COL_EXCLUDED_DELETE_PATH = "COL_EXCLUDED_DELETE_PATH"
    const val COL_SRC_PATH = "COL_SRC_PATH"
    const val FRAME_EXCLUDED_COPY_PATH = "FRAME_EXCLUDED_COPY_PATH"
    const val FRAME_EXCLUDED_DELETE_PATH = "FRAME_EXCLUDED_DELETE_PATH"
    const val FRAME_INCLUDED_PATH = "FRAME_INCLUDED_PATH"
    const val LB_ACTIVE = "LB_ACTIVE"
    const val LB_ALLOW_CONCURRENT_RUNS = "LB_ALLOW_CONCURRENT_RUNS"
    const val LB_AUTO_POPUP_LOGS = "LB_AUTO_POPUP_LOGS"
    const val LB_CLEAR_BEFORE_SYNCHRO = "LB_CLEAR_BEFORE_SYNCHRO"
    const val LB_CREATE_MISSING_DIRS = "LB_CREATE_MISSING_DIRS"
    const val LB_DEST_PATH = "LB_DEST_PATH"
    const val LB_DELETE_OBSOLETE = "LB_DELETE_OBSOLETE"
    const val LB_EXCLUDED_COPY_PATH = "LB_EXCLUDED_COPY_PATH"
    const val LB_EXCLUDED_DELETE_PATH = "LB_EXCLUDED_DELETE_PATH"
    const val LB_FONT_FAMILY = "LB_FONT_FAMILY"
    const val LB_FONT_SIZE = "LB_FONT_SIZE"
    const val LB_NAME = "LB_NAME"
    const val LB_NO_ACTIVE_TARGET = "LB_NO_ACTIVE_TARGET"
    const val LB_STORE_RELATIVE_PATHS = "LB_STORE_RELATIVE_PATHS"
    const val LB_SAVE_BEFORE_COPY = "LB_SAVE_BEFORE_COPY"
    const val LB_COPY_ON_SAVE = "LB_COPY_ON_SAVE"
    const val LB_SHOW_EXCLUDED_PATHS = "LB_SHOW_EXCLUDED_PATHS"
    const val LB_SHOW_IDENTICAL_PATHS = "LB_SHOW_IDENTICAL_PATHS"
    const val LB_SIMULATION_MODE = "LB_SIMULATION_MODE"
    const val LB_SHOW_SOURCE_PATHS = "LB_SHOW_SOURCE_PATHS"
    const val LB_SRC_PATH = "LB_SRC_PATH"
    const val LB_TITLE_IMPORT_SELECT = "LB_TITLE_IMPORT_SELECT"
    const val LB_DESC_IMPORT_SELECT = "LB_DESC_IMPORT_SELECT"
    const val MSG_EXPORT_FAILED = "MSG_EXPORT_FAILED"
    const val MSG_IMPORT_FAILED = "MSG_IMPORT_FAILED"
    const val MSG_CANT_DELETE_DIR = "MSG_CANT_DELETE_DIR"
    const val MSG_CANT_DELETE_FILE = "MSG_CANT_DELETE_FILE"
    const val MSG_CANT_COPY_FILE = "MSG_CANT_COPY_FILE"
    const val MSG_COPY_INTERRUPTED = "MSG_COPY_INTERRUPTED"
    const val MSG_COPY_STOPPED = "MSG_COPY_STOPPED"
    const val MSG_FROM = "MSG_FROM"
    const val MSG_INVALID_DEST_PATH = "MSG_INVALID_DEST_PATH"
    const val MSG_INVALID_PATH = "MSG_INVALID_PATH"
    const val MSG_INVALID_SRC_PATH = "MSG_INVALID_SRC_PATH"
    const val MSG_NB_FAILURE = "MSG_NB_FAILURE"
    const val MSG_NB_FAILURES = "MSG_NB_FAILURES"
    const val MSG_NB_FILES_COPIED = "MSG_NB_FILES_COPIED"
    const val MSG_NB_FILES_IGNORED = "MSG_NB_FILES_IGNORED"
    const val MSG_NB_FILE_COPIED = "MSG_NB_FILE_COPIED"
    const val MSG_NB_FILE_DELETED = "MSG_NB_FILE_DELETED"
    const val MSG_NB_FILES_DELETED = "MSG_NB_FILES_DELETED"
    const val MSG_NB_FILE_IGNORED = "MSG_NB_FILE_IGNORED"
    const val MSG_NO_FILE_COPIED = "MSG_NO_FILE_COPIED"
    const val MSG_PATH_NOT_FOUND = "MSG_PATH_NOT_FOUND"
    const val MSG_CANT_MAKE_DIRS = "MSG_CANT_MAKE_DIRS"
    const val MSG_PATH_NOT_IN_PROJECT = "MSG_PATH_NOT_IN_PROJECT"
    const val MSG_SRC_IN_DEST_PATH = "MSG_SRC_IN_DEST_PATH"
    const val MSG_SIMULATION_ACTIVED = "MSG_SIMULATION_ACTIVED"
    const val MSG_SIMULATION_DEACTIVED = "MSG_SIMULATION_DEACTIVED"
    const val MSG_SYMBOLS = "MSG_SYMBOLS"
    const val MSG_SYMBOL_DELETED = "MSG_SYMBOL_DELETED"
    const val MSG_SYMBOL_EQUAL = "MSG_SYMBOL_EQUAL"
    const val MSG_SYMBOL_EXCLUDED = "MSG_SYMBOL_EXCLUDED"
    const val MSG_SYMBOL_NEW = "MSG_SYMBOL_NEW"
    const val MSG_SYMBOL_NO_CLASS = "MSG_SYMBOL_NO_CLASS"
    const val MSG_SYMBOL_REPLACE = "MSG_SYMBOL_REPLACE"
    const val PANEL_EXCLUDED_COPY_PATHS = "PANEL_EXCLUDED_COPY_PATHS"
    const val PANEL_EXCLUDED_DELETE_PATHS = "PANEL_EXCLUDED_DELETE_PATHS"
    const val PANEL_FONT = "PANEL_FONT"
    const val PANEL_GENERAL = "PANEL_GENERAL"
    const val PANEL_INCLUDED_PATHS = "PANEL_INCLUDED_PATHS"
    const val PANEL_LOGS = "PANEL_LOGS"
    const val PANEL_SHOW_PATHS = "PANEL_SHOW_PATHS"
    const val PANEL_TARGETS = "PANEL_TARGETS"
    const val TITLE_DEFAULT_TARGET_NAME = "TITLE_DEFAULT_TARGET_NAME"
    const val TITLE_NEW_TARGET_NAME = "TITLE_NEW_TARGET_NAME"
    const val TITLE_CONSOLE_BUSY = "TITLE_CONSOLE_BUSY"
    const val TITLE_CONSOLE_INTERRUPTED = "TITLE_CONSOLE_INTERRUPTED"

    /**
     * Provides label for specified key
     */
    @JvmStatic
    operator fun get(key: String): String {
        return try {
            FileSyncBundle.getAdaptedMessage(key)
        } catch (e: MissingResourceException) {
            "?$key?"
        }
    }

    @JvmStatic
    operator fun get(key: String, vararg replaceStrings: Any?): String {
        return FileSyncBundle.getAdaptedMessage(key, replaceStrings);
    }
}