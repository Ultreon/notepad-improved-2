package me.qboi.texteditor.util

operator fun Number.plus(s: String): String {
    return toString() + s
}
