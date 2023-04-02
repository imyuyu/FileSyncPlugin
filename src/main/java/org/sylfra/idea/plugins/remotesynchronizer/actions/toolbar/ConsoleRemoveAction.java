package org.sylfra.idea.plugins.remotesynchronizer.actions.toolbar;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.imyuyu.idea.plugins.filesync.FileSyncPlugin;
import org.sylfra.idea.plugins.remotesynchronizer.ui.ThreadConsole;
import org.sylfra.idea.plugins.remotesynchronizer.utils.Utils;

/**
 * Remove the current console
 */
public class ConsoleRemoveAction extends AnAction
{
  public void actionPerformed(AnActionEvent e)
  {
    FileSyncPlugin plugin = Utils.getPlugin(e);
    ThreadConsole console = plugin.consolePane.removeCurrentConsole();
    plugin.copierThreadManager.removeThread(console.getThread());
  }

  public void update(AnActionEvent e)
  {
    FileSyncPlugin plugin = Utils.getPlugin(e);
    e.getPresentation().setEnabled((plugin != null)
      && (plugin.consolePane.getComponentCount() > 1)
      && (plugin.consolePane.getCurrentConsole() != null)
      && (plugin.consolePane.getCurrentConsole().getThread().isAvailable())
      && (!plugin.consolePane.getCurrentConsole().isMainConsole()));
  }
}
