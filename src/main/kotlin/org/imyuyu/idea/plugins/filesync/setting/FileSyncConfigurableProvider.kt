package org.imyuyu.idea.plugins.filesync.setting

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.options.ConfigurableProvider
import com.intellij.openapi.project.Project

/**
 * project configurable  provider
 * <p>
 * date: 2023/3/19 22:12
 *
 * @author imyuyu
 */
class FileSyncConfigurableProvider(private val project: Project) : ConfigurableProvider() {

    override fun createConfigurable(): Configurable? {
        var fileSyncConfigurable = FileSyncConfigurable(project)
        fileSyncConfigurable.createComponent();
        return fileSyncConfigurable;
    }

}