package org.sylfra.idea.plugins.remotesynchronizer.ui;

import com.intellij.openapi.wm.ToolWindowManager;
import org.imyuyu.idea.plugins.filesync.FileSyncPlugin;
import org.sylfra.idea.plugins.remotesynchronizer.synchronizing.SynchronizerThread;
import org.imyuyu.idea.plugins.filesync.utils.LabelsFactory;

import javax.swing.*;

/**
 * Thread consoles container
 */
public class ThreadConsolePane extends JTabbedPane
{
  private FileSyncPlugin plugin;

  public ThreadConsolePane(FileSyncPlugin plugin)
  {
    this.plugin = plugin;
  }

  public ThreadConsole createConsole(FileSyncPlugin plugin,
                                     SynchronizerThread copierThread, boolean findPosition)
  {
    String consoleName = findConsoleName(copierThread);
    ThreadConsole console = new ThreadConsole(this, plugin.getConfig(), copierThread,
      consoleName);
    plugin.getConfig().addConfigListener(console);
    console.setMainConsole(findMainConsole(console));

    copierThread.setConsole(console);

    addConsole(console, findPosition);

    return console;
  }

  protected FileSyncPlugin getPlugin()
  {
    return plugin;
  }

  public void addConsole(ThreadConsole console, boolean findPosition)
  {
    console.setTitle(findConsoleName(console.getThread()));
    int index = (findPosition)
      ? findConsolePosition(console)
      : getComponentCount();

    insertTab(findTitle(console), null, console, null, index);
  }

  private int findConsolePosition(ThreadConsole console)
  {
    ThreadConsole mainConsole = findMainConsole(console);
    if (mainConsole == null)
      return getComponentCount();

    int start = indexOfComponent(mainConsole);
    for (int i = start + 1; i < getComponentCount(); i++)
    {
      ThreadConsole c = (ThreadConsole) getComponentAt(i);
      if (!c.hasSameTargetMappings(console))
        return i;
    }

    return getComponentCount();
  }

  private ThreadConsole findMainConsole(ThreadConsole console)
  {
    for (int i = 0; i < getComponentCount(); i++)
    {
      ThreadConsole c = (ThreadConsole) getComponentAt(i);
      if ((c.isMainConsole()) && (c.hasSameTargetMappings(console)))
        return c;
    }

    return null;
  }

  private String findConsoleName(SynchronizerThread thread)
  {
    boolean found = false;
    String result = thread.getTargetMappings().getName();
    for (int i = 0; i < getComponentCount(); i++)
    {
      ThreadConsole console = (ThreadConsole) getComponentAt(i);
      if (console.getTitle().equals(result))
      {
        found = true;
        break;
      }
    }

    if (!found)
      return result;

    int inc = 1;
    result = thread.getTargetMappings().getName() + " (" + inc + ")";
    for (int i = 0; i < getComponentCount(); i++)
    {
      ThreadConsole console = (ThreadConsole) getComponentAt(i);
      if (console.getTitle().equals(result))
      {
        i = 0;
        result = thread.getTargetMappings().getName() + " (" + ++inc + ")";
      }
    }

    return result;
  }

  public ThreadConsole getConsole(int i)
  {
    return (ThreadConsole) getComponentAt(i);
  }

  public ThreadConsole getCurrentConsole()
  {
    return (ThreadConsole) getSelectedComponent();
  }

  public ThreadConsole removeCurrentConsole()
  {
    ThreadConsole console = getCurrentConsole();
    remove(console);
    return console;
  }

  public void removeConsole(SynchronizerThread thread)
  {
    remove(thread.getConsole());
  }

  public void updateTitle(final ThreadConsole console)
  {
    SwingUtilities.invokeLater(new Runnable()
    {
      public void run()
      {
        setTitleAt(indexOfComponent(console), findTitle(console));
      }
    });
  }

  private String findTitle(ThreadConsole console)
  {
    String title = console.getTitle();

    if (console.getThread().isActive())
      title += " " + LabelsFactory.get(LabelsFactory.TITLE_CONSOLE_BUSY);
    else if (console.getThread().isInterrupted())
      title += " "
        + LabelsFactory.get(LabelsFactory.TITLE_CONSOLE_INTERRUPTED);

    return title;
  }

  public void doPopup()
  {
    ToolWindowManager.getInstance(plugin.project)
      .getToolWindow(FileSyncPlugin.PLUGIN_NAME)
      .show(null);
  }
}
