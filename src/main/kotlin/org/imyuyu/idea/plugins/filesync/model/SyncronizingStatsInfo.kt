package org.imyuyu.idea.plugins.filesync.model

/**
 * Store copy results,  ie. numbers of success, failures, ...
 */
class SyncronizingStatsInfo {
    var successCount = 0
        private set
    var failuresCount = 0
        private set
    var ignoredCount = 0
        private set
    var excludedCount = 0
        private set
    var deletedCount = 0
        private set

    fun addSuccess() {
        successCount++
    }

    fun addFailure() {
        failuresCount++
    }

    fun addIgnored() {
        ignoredCount++
    }

    fun addExcluded() {
        excludedCount++
    }

    fun addDeleted() {
        deletedCount++
    }

    fun hasFailures(): Boolean {
        return failuresCount > 0
    }
}
