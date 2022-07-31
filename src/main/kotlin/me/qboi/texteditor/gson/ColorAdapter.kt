package me.qboi.texteditor.gson

import com.google.gson.JsonParseException
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.awt.Color
import java.io.IOException

object ColorAdapter : TypeAdapter<Color>() {
    @Throws(IOException::class)
    override fun read(reader: JsonReader): Color? {
        if (reader.peek() === JsonToken.NULL) {
            reader.nextNull()
            return null
        }
        val hex: String = reader.nextString()
        if (!hex.startsWith('#')) {
            throw JsonParseException("Color must start with #")
        }
        if (!hex.matches("#[0-9a-fA-F]{8}".toRegex())) {
            throw JsonParseException("Color must be 6 hex digits")
        }
        return Color(hex.substring(1).trimStart('0').toUInt(16).toInt())
    }

    @Throws(IOException::class)
    override fun write(writer: JsonWriter, value: Color?) {
        if (value == null) {
            writer.nullValue()
            return
        }
        val hexNr = value.rgb.toUInt().toString(16).padStart(8, '0')
        val hexColor = "#$hexNr"
        writer.value(hexColor)
    }
}
