package org.imyuyu.idea.plugins.filesync.utils

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.JDOMUtil
import com.intellij.util.xmlb.XmlSerializer
import org.jdom.Document
import org.jdom.JDOMException
import org.imyuyu.idea.plugins.filesync.FileSyncPlugin
import org.sylfra.idea.plugins.remotesynchronizer.model.Config
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 *
 */
class ConfigExternalizer protected constructor(private val project: Project) {


    @Throws(IOException::class)
    fun write(dest: File?) {
        val config = FileSyncPlugin.getInstance(project).config
        val element = XmlSerializer.serialize(config)
        val document = Document(element)
        FileOutputStream(dest).use { stream ->
            JDOMUtil.writeDocument(
                document,
                stream,
                System.getProperty("line.separator")
            )
        }
    }

    @Throws(IOException::class, JDOMException::class)
    fun read(src: File) {
        val document = JDOMUtil.load(src.toPath())
        val config = XmlSerializer.deserialize(document, Config::class.java)
        FileSyncPlugin.getInstance(project).stateComponent.loadState(config)
    }
}
