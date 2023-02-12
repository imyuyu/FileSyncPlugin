package org.sylfra.idea.plugins.remotesynchronizer.utils;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.JDOMUtil;
import com.intellij.util.xmlb.SkipDefaultValuesSerializationFilters;
import com.intellij.util.xmlb.XmlSerializer;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.sylfra.idea.plugins.remotesynchronizer.FileSyncPlugin;
import org.sylfra.idea.plugins.remotesynchronizer.model.Config;

import java.io.*;

/**
 *
 */
public class ConfigExternalizer
{
  private final Project project;

  protected ConfigExternalizer(Project project)
  {
    this.project = project;
  }

  public void write(File dest) throws IOException
  {
    Config config = FileSyncPlugin.getInstance(project).getConfig();

    Element element = XmlSerializer.serialize(config);
    Document document = new Document(element);

    try(OutputStream stream = new FileOutputStream(dest))
    {
      JDOMUtil.writeDocument(document, stream, System.getProperty("line.separator"));
    }
  }

  public void read(File src) throws IOException, JDOMException
  {
    Element document = JDOMUtil.load(src.toPath());
    Config config = XmlSerializer.deserialize(document, Config.class);
    FileSyncPlugin.getInstance(project).getStateComponent().loadState(config);
  }
}
