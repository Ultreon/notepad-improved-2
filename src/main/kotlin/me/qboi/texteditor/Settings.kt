package me.qboi.texteditor

import java.awt.Font

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

    //    lateinit var theme: SettingsConfiguration
    lateinit var theme: String

//    private val logger = Logger.getLogger("Settings")
//
//    private val settingsFile = File("settings.json")
//    private val gson = GsonBuilder().setPrettyPrinting().serializeNulls().disableHtmlEscaping().also {
//        it.registerTypeHierarchyAdapter(Font::class.java, FontAdapter)
//        it.registerTypeHierarchyAdapter(Color::class.java, ColorAdapter)
//        it.registerTypeHierarchyAdapter(Point::class.java, PointAdapter)
//        it.registerTypeHierarchyAdapter(Theme::class.java, ThemeAdapter)
//        it.registerTypeAdapter(SettingsConfiguration::class.java, SettingsConfigurationInstanceCreator)
//    }.create()
}
