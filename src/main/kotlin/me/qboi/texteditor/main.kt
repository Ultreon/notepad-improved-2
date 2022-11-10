package me.qboi.texteditor

import me.qboi.texteditor.lang.LanguageManager
import me.qboi.texteditor.main.AppPrefs
import me.qboi.texteditor.main.MainFrame
import org.oxbow.swingbits.dialog.task.TaskDialogs
import java.lang.Thread.sleep
import javax.swing.SwingUtilities
import javax.swing.UIManager
import kotlin.system.exitProcess

fun main() {
    AppPrefs.init(appId)
    AppPrefs.setupLaf(arrayOf())

    Thread.setDefaultUncaughtExceptionHandler { _, ex ->
        crash(ex)
    }
    LanguageManager.registerDefaults()
    LanguageManager.freeze()

    do {
        MainFrame.isRunning = true
        SwingUtilities.invokeAndWait {
            MainFrame.start()
        }
        while (MainFrame.isRunning) {
            sleep(50)
        }
    } while (isRestart)
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
