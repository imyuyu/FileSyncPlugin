package org.sylfra.idea.plugins.remotesynchronizer;

import com.intellij.openapi.components.ServiceKt;
import com.intellij.openapi.components.impl.stores.IProjectStore;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.sylfra.idea.plugins.remotesynchronizer.javasupport.IJavaSupport;
import org.sylfra.idea.plugins.remotesynchronizer.javasupport.NoJavaSupport;
import org.sylfra.idea.plugins.remotesynchronizer.model.Config;
import org.sylfra.idea.plugins.remotesynchronizer.synchronizing.SynchronizerThreadManager;
import org.sylfra.idea.plugins.remotesynchronizer.ui.ThreadConsolePane;
import org.sylfra.idea.plugins.remotesynchronizer.ui.ToolPanel;
import org.sylfra.idea.plugins.remotesynchronizer.ui.config.ConfigPanel;
import org.sylfra.idea.plugins.remotesynchronizer.utils.ConfigExternalizer;
import org.sylfra.idea.plugins.remotesynchronizer.utils.ConfigPathsManager;
import org.sylfra.idea.plugins.remotesynchronizer.utils.ConfigStateComponent;

import javax.swing.*;
import java.net.URL;
import java.util.Objects;

/**
 * Plugin main class
 */
public class FileSyncPlugin
{
  public static final String PLUGIN_NAME = "FileSync";

  public static final String WINDOW_ACTIONS_NAME = PLUGIN_NAME + "Window";
  public static final String ACTION_CLEAR_CONSOLE_NAME = "ConsoleClearAction";
  public static final String ACTION_REMOVE_CONSOLE_NAME = "ConsoleRemoveAction";
  public static final String ACTION_INTERRUPT_THREAD_NAME = "ThreadInterruptAction";
  public static final String ACTION_STOP_THREAD_NAME = "ThreadStopAction";
  public static final String ACTION_RERUN_LAST_SYNCHRO_NAME = "RerunLastSynchroAction";

  // Current project
  private Project project;
  // Provides support for paths management
  private ConfigPathsManager pathManager;
  // Manage threaded copies
  private SynchronizerThreadManager copierThreadManager;
  // Contains different consoles
  private ThreadConsolePane consolePane;

  private IJavaSupport javaSupport;

  public FileSyncPlugin(Project project)
  {
    this.project = project;

    pathManager = new ConfigPathsManager(this);
    javaSupport = project.getService(IJavaSupport.class);
    if (javaSupport == null)
    {
      javaSupport = new NoJavaSupport();
    }

    consolePane = new ThreadConsolePane(this);
    copierThreadManager = new SynchronizerThreadManager(this);
  }

  public Project getProject()
  {
    return project;
  }

  public VirtualFile getProjectBaseDir(){
    IProjectStore projectStore = (IProjectStore) ServiceKt.getStateStore(project);

    VirtualFile projectDir = VirtualFileManager.getInstance().findFileByNioPath(projectStore.getProjectBasePath());

    return projectDir;
  }

  public Config getConfig()
  {
    return getStateComponent().getState();
  }

  public SynchronizerThreadManager getCopierThreadManager()
  {
    return copierThreadManager;
  }

  public ConfigPathsManager getPathManager()
  {
    return pathManager;
  }

  public ThreadConsolePane getConsolePane()
  {
    return consolePane;
  }

  public Icon getIcon()
  {
    return new ImageIcon(getResource("logo-big.png"));
  }


  public void projectOpened()
  {

  }

  public void projectClosed()
  {
  }

  public static FileSyncPlugin getInstance(Project project)
  {
    return project.getService(FileSyncPlugin.class);
  }

  public ConfigExternalizer getConfigExternalizer()
  {
    return project.getService(ConfigExternalizer.class);
  }

  public IJavaSupport getJavaSupport()
  {
    return javaSupport;
  }

  public static URL getResource(String relativePath)
  {
    return FileSyncPlugin.class.getResource("resources/" + relativePath);
  }

  /**
   * Provides settings component
   *
   * @return settings component
   */
  public ConfigStateComponent getStateComponent()
  {
    return project.getService(ConfigStateComponent.class);
  }

  public void launchSyncIfAllowed(VirtualFile[] files)
  {
    // Check if configuration allows concurrent runs when a synchro is running
    if ((!Objects.requireNonNull(getStateComponent().getState()).getGeneralOptions().isAllowConcurrentRuns())
      && (copierThreadManager.hasRunningSynchro()))
    {
      consolePane.doPopup();
      return;
    }

    copierThreadManager.launchSynchronization(files);
  }
}
