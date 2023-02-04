package org.sylfra.idea.plugins.remotesynchronizer.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.vfs.VirtualFile;
import org.sylfra.idea.plugins.remotesynchronizer.FileSyncPlugin;
import org.sylfra.idea.plugins.remotesynchronizer.utils.Utils;

/**
 * Synchronize selected files
 */
public class RemoteSynchronizeSelectedAction extends AbstractRemoteSynchronizeAction
{
  protected VirtualFile[] getFiles(FileSyncPlugin plugin,
                                   DataContext dataContext)
  {
    return plugin.getJavaSupport().getSelectedFiles(dataContext);
  }

  public boolean isEnabled(AnActionEvent e)
  {
    // Some files must be selected
    return super.isEnabled(e)
      && (Utils.getPlugin(e).getJavaSupport().insideModule(e.getDataContext())
        || (CommonDataKeys.VIRTUAL_FILE_ARRAY.getData(e.getDataContext()) != null));
  }
}
