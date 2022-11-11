package com.ultreon.notepadimproved.util

operator fun Number.plus(s: String): String {
    return toString() + s
}
