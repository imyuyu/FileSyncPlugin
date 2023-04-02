package org.imyuyu.idea.plugins.filesync

import org.jetbrains.annotations.PropertyKey

/**
 *
 *
 * date: 2023/3/31 22:33
 *
 * @author Zhengyu Hu
 */
const val BUNDLE = "messages.Labels"

object FileSyncBundle : FileSyncDynamicBundle(BUNDLE)

fun message(@PropertyKey(resourceBundle = BUNDLE) key: String, vararg params: Any): String {
    return FileSyncBundle.getMessage(key, *params)
}

fun adaptedMessage(@PropertyKey(resourceBundle = BUNDLE) key: String, vararg params: Any): String {
    return FileSyncBundle.getAdaptedMessage(key, *params)
}