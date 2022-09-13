package me.qboi.texteditor

import org.oxbow.swingbits.dialog.task.TaskDialogs
import javax.swing.SwingUtilities
import javax.swing.UIManager
import kotlin.system.exitProcess

fun main() {
    AppPrefs.init("me.qboi.texteditor")
    AppPrefs.setupLaf(arrayOf())

    SwingUtilities.invokeLater {
        Thread.setDefaultUncaughtExceptionHandler { _, ex ->
            crash(ex)
        }
        MainFrame.start()
    }
}

@Throws(Exception::class)
fun crash(e: Throwable) {
    try {
        MainFrame.instance.dispose()
    } catch (e: Exception) {
        // ignore
    }
    SwingUtilities.invokeLater {
        val lookAndFeel = UIManager.getLookAndFeel()
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
        e.printStackTrace()
        TaskDialogs.showException(e)
        UIManager.setLookAndFeel(lookAndFeel)
        exitProcess(1)
    }
}
