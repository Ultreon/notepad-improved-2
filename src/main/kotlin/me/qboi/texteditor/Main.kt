package me.qboi.texteditor

import java.io.File
import javax.swing.SwingUtilities

fun main(args: Array<String>) {
    var file: File? = null
    if (args.size == 1) {
        file = File(args[0])
    }

//    LafManager.install()

    AppPrefs.init("me.qboi.texteditor")
    AppPrefs.setupLaf(arrayOf())

    SwingUtilities.invokeLater {
        Editor(file)
    }
}