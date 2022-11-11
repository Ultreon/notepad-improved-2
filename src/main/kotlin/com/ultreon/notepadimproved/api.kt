package com.ultreon.notepadimproved

import com.google.gson.Gson
import com.ultreon.notepadimproved.lang.LanguageManager
import java.awt.event.ActionEvent
import javax.swing.AbstractAction
import javax.swing.Action

fun action(key: String?): Action {
    return action(key) { }
}

fun action(key: String?, function: () -> Unit): Action {
    return object : AbstractAction(key) {
        private val TRANSLATION_KEY: String = "TranslationKey"

        init {
            putValue(TRANSLATION_KEY, key)
        }

        override fun actionPerformed(e: ActionEvent) {
            function()
        }

        override fun getValue(key: String?): Any? {
            if (key == NAME) {
                val key1 = super.getValue(TRANSLATION_KEY)?.toString()
                return if (key1 == null) "" else LanguageManager[key1]
            }
            return super.getValue(key)
        }
    }
}

val gson = Gson()
var isRestart = false
    get() {
        val value = field
        field = false
        return value
    }
