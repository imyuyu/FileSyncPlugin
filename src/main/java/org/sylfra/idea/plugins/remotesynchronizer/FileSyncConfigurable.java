package org.sylfra.idea.plugins.remotesynchronizer;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsContexts;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;
import org.sylfra.idea.plugins.remotesynchronizer.model.Config;
import org.sylfra.idea.plugins.remotesynchronizer.ui.config.ConfigPanel;
import org.sylfra.idea.plugins.remotesynchronizer.utils.ConfigStateComponent;

import javax.swing.*;

/**
 * config
 * <p>
 * date: 2023/3/5 18:56
 *
 * @author imyuyu
 */
public class FileSyncConfigurable implements Configurable {

    private final Project project;
    // Settings panel
    private ConfigPanel panel;

    public FileSyncConfigurable(Project project) {
        this.project = project;
    }

    @Override
    public @Nullable @NonNls String getHelpTopic() {
        return null;
    }

    @Override
    public @NlsContexts.ConfigurableName String getDisplayName() {
        return FileSyncPlugin.PLUGIN_NAME;
    }

    @Override
    public @Nullable JComponent createComponent() {
        panel = new ConfigPanel(project.getService(FileSyncPlugin.class));

        return panel;
    }

    @Override
    public boolean isModified() {
        return panel.isModified(getConfig());
    }

    @Override
    public void apply() throws ConfigurationException {
        Config config = getConfig();

        panel.apply(config);
        config.fireConfigChanged();
    }

    public void reset()
    {
        panel.reset(getConfig());
    }

    public void disposeUIResources()
    {
        panel = null;
    }

    public Config getConfig()
    {
        return getStateComponent().getState();
    }

    public ConfigStateComponent getStateComponent()
    {
        return project.getService(ConfigStateComponent.class);
    }
}
