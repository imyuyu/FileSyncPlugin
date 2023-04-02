package org.sylfra.idea.plugins.remotesynchronizer.utils;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import org.imyuyu.idea.plugins.filesync.FileSyncPlugin;
import org.sylfra.idea.plugins.remotesynchronizer.synchronizing.SynchronizerThread;
import org.sylfra.idea.plugins.remotesynchronizer.ui.ThreadConsole;

import javax.swing.*;
import java.awt.*;
import java.io.*;

/**
 * Misc utility methods
 */
public class Utils
{
  public static Dimension adjustComponentSizesToMax(JComponent c1,
    JComponent c2)
  {
    return adjustComponentSizesToMax(new JComponent[]{c1, c2});
  }

  public static Dimension adjustComponentSizesToMax(JComponent[] l)
  {
    int width = 0;
    int height = 0;
    for (JComponent aL : l)
    {
      width = Math.max(width, aL.getPreferredSize().width);
      height = Math.max(height, aL.getPreferredSize().height);
    }
    Dimension d = new Dimension(width, height);

    for (JComponent aL : l)
    {
      aL.setPreferredSize(d);
    }

    return d;
  }

  public static void copyFile(File srcFile, File destFile)
    throws IOException
  {
    if (!srcFile.exists())
      return;

    FileInputStream in = new FileInputStream(srcFile);
    FileOutputStream out = new FileOutputStream(destFile);
    byte buffer[] = new byte[8192];
    int count = 0;
    do
    {
      out.write(buffer, 0, count);
      count = in.read(buffer, 0, buffer.length);
    }
    while (count != -1);

    in.close();
    out.close();

    destFile.setLastModified(srcFile.lastModified());
  }

  public static FileSyncPlugin getPlugin(AnActionEvent e)
  {
    Project project = e.getData(CommonDataKeys.PROJECT);
    return (project == null)
      ? null
      : FileSyncPlugin.getInstance(project);
  }

  public static SynchronizerThread getCurrentCopierThread(AnActionEvent e)
  {
    FileSyncPlugin plugin = getPlugin(e);
    if (plugin == null)
      return null;

    ThreadConsole currentConsole = plugin.consolePane.getCurrentConsole();
    if (currentConsole == null)
      return null;

    return currentConsole.getThread();
  }

  public final static class DirectoriesFilter implements FileFilter
  {
    public boolean accept(File f)
    {
      return f.isDirectory();
    }
  }
}
