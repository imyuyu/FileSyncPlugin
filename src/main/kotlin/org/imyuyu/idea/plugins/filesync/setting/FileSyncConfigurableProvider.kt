package org.imyuyu.idea.plugins.filesync.setting

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.options.ConfigurableProvider
import com.intellij.openapi.project.Project
import java.awt.GraphicsEnvironment

/**
 * project configurable  provider
 * <p>
 * date: 2023/3/19 22:12
 *
 * @author imyuyu
 */
class FileSyncConfigurableProvider(private val project: Project) : ConfigurableProvider() {

    override fun createConfigurable(): Configurable? {
        return FileSyncConfigurable(project);
    }

}