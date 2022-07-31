package me.qboi.texteditor.gson

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import me.qboi.texteditor.util.plus
import java.awt.Point
import java.io.IOException

object PointAdapter : TypeAdapter<Point>() {
    @Throws(IOException::class)
    override fun read(reader: JsonReader): Point? {
        if (reader.peek() === JsonToken.NULL) {
            reader.nextNull()
            return null
        }
        val xy: String = reader.nextString()
        val parts = xy.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val x = parts[0].toInt()
        val y = parts[1].toInt()
        return Point(x, y)
    }

    @Throws(IOException::class)
    override fun write(writer: JsonWriter, value: Point?) {
        if (value == null) {
            writer.nullValue()
            return
        }
        val xy: String = value.getX() + "," + value.getY()
        writer.value(xy)
    }
}
