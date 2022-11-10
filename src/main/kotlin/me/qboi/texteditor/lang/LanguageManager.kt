package me.qboi.texteditor.lang

import java.util.*

object LanguageManager {
    private var frozen: Boolean = false
    private val instances = mutableMapOf<Locale, Language>()
    private val deferredRegister = mutableListOf<Language>()
    private val fallback: Language = register("en-US")

    @Suppress("SameParameterValue")
    private fun register(tag: String): Language {
        return register(Locale.forLanguageTag(tag))
    }

    var current: Language = fallback
    var language: Locale
        get() = current.locale
        set(value) {
            current = requireNotNull(instances[value]) { "Language not found: $value" }
        }

    lateinit var registry: Array<Language>
        private set

    fun register(locale: Locale): Language {
        check(!frozen) { "Language registry is frozen." }
        return Language(locale).also {
            instances[locale] = it
            deferredRegister += it
        }
    }

    fun freeze() {
        frozen = true
        registry = deferredRegister.toTypedArray()
        for (language in registry) {
            language.load()
        }
    }

    operator fun get(key: String): String {
        return current[key] ?: fallback[key] ?: key
    }

    operator fun invoke(key: String, vararg args: Any?): String {
        return current.format(key, args) ?: fallback.format(key, args) ?: key
    }

    fun registerDefaults() {
        register("nl-NL")
        register("de-DE")
        register("fr-FR")
        register("es-ES")
        register("zh-CN")
    }
}