package org.sylfra.idea.plugins.remotesynchronizer.ui.config;

import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBTabbedPane;
import org.imyuyu.idea.plugins.filesync.FileSyncPlugin;
import org.sylfra.idea.plugins.remotesynchronizer.model.Config;
import org.sylfra.idea.plugins.remotesynchronizer.ui.config.panes.GeneralPane;
import org.sylfra.idea.plugins.remotesynchronizer.ui.config.panes.IConfigPane;
import org.sylfra.idea.plugins.remotesynchronizer.ui.config.panes.LogPane;
import org.sylfra.idea.plugins.remotesynchronizer.ui.config.panes.TargetsPane;
import org.sylfra.idea.plugins.remotesynchronizer.utils.ConfigPathsManager;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

/**
 * Configuration panel
 */
public class ConfigPanel extends JPanel
{
  private Project currentProject;
  private IConfigPane[] configPanes;

  public ConfigPanel(FileSyncPlugin plugin)
  {
    configPanes = new IConfigPane[]{
      new TargetsPane(plugin.pathManager),
      new GeneralPane(plugin.project, plugin.pathManager),
      new LogPane()
    };

    buildUI(plugin.pathManager);
    currentProject = plugin.project;
  }

  public Project getCurrentProject()
  {
    return currentProject;
  }

  public boolean isModified(Config config)
  {
    for (IConfigPane configPane : configPanes)
    {
      if (configPane.isModified(config))
      {
        return true;
      }
    }

    return false;
  }

  public void reset(Config config)
  {
    for (IConfigPane configPane : configPanes)
    {
      configPane.reset(config);
    }
  }

  public void apply(Config config)
  {
    for (IConfigPane configPane : configPanes)
    {
      configPane.apply(config);
    }
  }

  private void buildUI(ConfigPathsManager pathsManager)
  {
    JPanel pnHeader = createHeaderPanel();

    JBTabbedPane mainPane = new JBTabbedPane();

    for (IConfigPane configPane : configPanes)
    {
      configPane.buildUI(pathsManager);
      mainPane.addTab(configPane.getTitle(), (Component) configPane);
    }

    setLayout(new BorderLayout());
    add(pnHeader, BorderLayout.NORTH);
    add(mainPane, BorderLayout.CENTER);
  }

  private JPanel createHeaderPanel()
  {
    JPanel pnImportExport = new JBPanel<>();

    pnImportExport.add(new JButton(new ActionsHolder.ImportAction(this)));
    pnImportExport.add(new JButton(new ActionsHolder.ExportAction(this)));

    JPanel panel = new JPanel(new BorderLayout());
    panel.add(pnImportExport, BorderLayout.CENTER);
    panel.add(new JBLabel("version " +
      Objects.requireNonNull(PluginManagerCore.getPlugin(PluginId.getId(FileSyncPlugin.PLUGIN_NAME))).getVersion()),
      BorderLayout.EAST);
    return panel;
  }
}
