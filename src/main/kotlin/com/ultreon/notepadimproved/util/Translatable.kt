package com.ultreon.notepadimproved.util

import com.ultreon.notepadimproved.lang.Language

interface Translatable {
    fun onLanguageChanged(language: Language)
}
