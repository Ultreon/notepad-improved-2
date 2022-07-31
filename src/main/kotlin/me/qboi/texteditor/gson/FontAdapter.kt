package me.qboi.texteditor.gson

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.awt.Font
import java.io.IOException

object FontAdapter : TypeAdapter<Font>() {
    @Throws(IOException::class)
    override fun read(reader: JsonReader): Font? {
        if (reader.peek() === JsonToken.NULL) {
            reader.nextNull()
            return null
        }
        val name: String = reader.nextString()
        return Font.decode(name)
    }

    @Throws(IOException::class)
    override fun write(writer: JsonWriter, value: Font?) {
        if (value == null) {
            writer.nullValue()
            return
        }
        val style = run {
            var style = "plain"
            if (value.isBold && value.isItalic) {
                style = "bolditalic"
            } else if (value.isBold) {
                style = "bold"
            } else if (value.isItalic) {
                style = "italic"
            }
            return@run style
        }
        writer.value("${value.name}-${style}-${value.size}")
    }
}
