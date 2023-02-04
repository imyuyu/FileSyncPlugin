package org.sylfra.idea.plugins.remotesynchronizer.actions.toolbar;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.sylfra.idea.plugins.remotesynchronizer.FileSyncPlugin;
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
    ThreadConsole console = plugin.getConsolePane().removeCurrentConsole();
    plugin.getCopierThreadManager().removeThread(console.getThread());
  }

  public void update(AnActionEvent e)
  {
    FileSyncPlugin plugin = Utils.getPlugin(e);
    e.getPresentation().setEnabled((plugin != null)
      && (plugin.getConsolePane().getComponentCount() > 1)
      && (plugin.getConsolePane().getCurrentConsole() != null)
      && (plugin.getConsolePane().getCurrentConsole().getThread().isAvailable())
      && (!plugin.getConsolePane().getCurrentConsole().isMainConsole()));
  }
}
