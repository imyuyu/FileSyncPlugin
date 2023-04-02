package icons

import com.intellij.openapi.util.IconLoader
import javax.swing.Icon

object FileSyncIcons {

    @JvmField
    val ToolWindowIcon: Icon = load("/icons/logo-small.png")

    @JvmField
    var TO_LEFT_ICON: Icon = load("/icons/left.png")

    @JvmField
    var TO_RIGHT_ICON: Icon = load("/icons/right.png")

    @JvmStatic
    fun load(path: String): Icon {
        return IconLoader.getIcon(path, FileSyncIcons::class.java)
    }
}