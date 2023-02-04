package org.sylfra.idea.plugins.remotesynchronizer.actions;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.vfs.VirtualFile;
import org.sylfra.idea.plugins.remotesynchronizer.FileSyncPlugin;

/**
 * Synchronize opened files
 */
public class RemoteSynchronizeOpenedAction extends AbstractRemoteSynchronizeAction
{
  protected VirtualFile[] getFiles(FileSyncPlugin plugin,
                                   DataContext dataContext)
  {
    return FileEditorManager.getInstance(plugin.getProject()).getOpenFiles();
  }
}
