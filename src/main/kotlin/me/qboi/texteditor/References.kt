package me.qboi.texteditor

object References {
    val APP_ICON: String = References::class.java.getResource("/icon.png")?.toString() ?: ""
    const val APP_NAME: String = "Notepad Improved"
    const val SOURCE_URL: String = "https://github.com/Ultreon/notepad-improved-2"
    const val ISSUES_URL: String = "https://github.com/Ultreon/notepad-improved-2/issues"
    const val NEW_ISSUE_URL: String = "https://github.com/Ultreon/notepad-improved-2/issues/new/choose"
}
