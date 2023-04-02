package org.sylfra.idea.plugins.remotesynchronizer.actions.toolbar;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.imyuyu.idea.plugins.filesync.FileSyncPlugin;
import org.sylfra.idea.plugins.remotesynchronizer.ui.ThreadConsole;
import org.sylfra.idea.plugins.remotesynchronizer.utils.Utils;

/**
 * Clear the current console
 */
public class ConsoleClearAction extends AnAction
{
  public void actionPerformed(AnActionEvent e)
  {
    ThreadConsole currentConsole = Utils.getPlugin(e).consolePane
      .getCurrentConsole();
    if (currentConsole != null)
      currentConsole.clear();
  }

  public void update(AnActionEvent e)
  {
    FileSyncPlugin plugin = Utils.getPlugin(e);
    e.getPresentation().setEnabled((plugin != null)
      && (plugin.consolePane.getCurrentConsole() != null)
      && (!plugin.consolePane.getCurrentConsole().isCleared()));
  }
}
