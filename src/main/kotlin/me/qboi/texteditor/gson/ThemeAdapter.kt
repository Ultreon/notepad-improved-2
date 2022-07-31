package me.qboi.texteditor.gson

import com.github.weisj.darklaf.LafManager
import com.github.weisj.darklaf.theme.Theme
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter

object ThemeAdapter : TypeAdapter<Theme>() {
    override fun read(reader: JsonReader): Theme? {
        if (reader.peek() === JsonToken.NULL) {
            reader.nextNull()
            return null
        }
        val name: String = reader.nextString()
        LafManager.getRegisteredThemes().toList().firstOrNull {
            it.name == name
        }?.let {
            return it
        } ?: return null
    }

    override fun write(writer: JsonWriter, value: Theme?) {
        if (value == null) {
            writer.nullValue()
            return
        }
        writer.value(value.name)
    }

}
