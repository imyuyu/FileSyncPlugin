package org.sylfra.idea.plugins.remotesynchronizer.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.sylfra.idea.plugins.remotesynchronizer.FileSyncPlugin;
import org.sylfra.idea.plugins.remotesynchronizer.utils.ConfigPathsManager;
import org.sylfra.idea.plugins.remotesynchronizer.utils.Utils;

/**
 * Common code for {@link RemoteSynchronizeOpenedAction} and
 * {@link RemoteSynchronizeSelectedAction}
 */
public abstract class AbstractRemoteSynchronizeAction extends AnAction
{
  /**
   * Copy selected/opened files
   */
  public void actionPerformed(AnActionEvent e)
  {
    final FileSyncPlugin plugin = Utils.getPlugin(e);
    final VirtualFile[] files = getFiles(plugin, e.getDataContext());
    if (files == null)
      return;

    if (plugin.getConfig().getGeneralOptions().isSaveBeforeCopy())
      FileDocumentManager.getInstance().saveAllDocuments();

    if (!plugin.getCopierThreadManager().hasRunningSynchro())
      refreshVfsIfJavaSelected(files, plugin.getPathManager());

    plugin.launchSyncIfAllowed(files);
  }

  private void refreshVfsIfJavaSelected(final VirtualFile[] files,
    final ConfigPathsManager pathManager)
  {
    ApplicationManager.getApplication().runWriteAction(new Runnable()
    {
      public void run()
      {
        for (int i = 0; i < files.length; i++)
          if (pathManager.isJavaSource(files[i]))
          {
            LocalFileSystem.getInstance().refresh(false);
            return;
          }
      }
    });
  }

  public void update(AnActionEvent e)
  {
    e.getPresentation().setEnabled(isEnabled(e));
  }

  protected boolean isEnabled(AnActionEvent e)
  {
    FileSyncPlugin plugin = Utils.getPlugin(e);
    return (plugin != null)
      && ((plugin.getConfig().getGeneralOptions().isAllowConcurrentRuns())
        || (!plugin.getCopierThreadManager().hasRunningSynchro()));
  }

  protected abstract VirtualFile[] getFiles(FileSyncPlugin plugin,
                                            DataContext dataContext);
}
