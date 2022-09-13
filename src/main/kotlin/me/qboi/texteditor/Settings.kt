package me.qboi.texteditor

import java.awt.Font
import java.io.File

object Settings {
    var font: Font
        get() = Font(AppPrefs.fontName, AppPrefs.fontStyle, AppPrefs.fontSize)
        set(value) {
            AppPrefs.fontName = value.name
            AppPrefs.fontStyle = value.style
            AppPrefs.fontSize = value.size
        }
    var wordWrap: Boolean
        get() = AppPrefs.wordWrap
        set(value) {
            AppPrefs.wordWrap = value
        }

    var recentFiles: List<File>
        get() = AppPrefs.recentFiles
        set(value) {
            AppPrefs.recentFiles = value
        }

    //    lateinit var theme: SettingsConfiguration
    lateinit var theme: String
}
