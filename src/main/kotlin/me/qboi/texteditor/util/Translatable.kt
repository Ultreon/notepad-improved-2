package me.qboi.texteditor.util

import me.qboi.texteditor.lang.Language

interface Translatable {
    fun onLanguageChanged(language: Language)
}
