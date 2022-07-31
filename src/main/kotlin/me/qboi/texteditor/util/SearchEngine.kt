package me.qboi.texteditor.util

import java.net.URI
import java.net.URLEncoder

enum class SearchEngine(val apply: (String) -> String) {
    GOOGLE({ "https://www.google.com/search?q=$it" }),
    YANDEX({ "https://yandex.ru/search/?text=$it" }),
    BING({ "https://www.bing.com/search?q=$it" }),
    DUCKDUCKGO({ "https://duckduckgo.com/?q=$it" }),
    YAHOO({ "https://search.yahoo.com/search?p=$it" }),
    YOUTUBE({ "https://www.youtube.com/results?search_query=$it" }),
    ;

    fun search(query: String): URI {
        val encode = URLEncoder.encode(query, "UTF-8")
        return URI(apply(encode))
    }
}