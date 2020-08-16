package utils

import java.io.File

public class MapIO {
    fun loadMapFromDisk(fileName: String) {
        MapIO::class.java.getResourceAsStream(fileName).buffered().reader().use{ reader ->
            println(reader.readText())
//        File(fileName).forEachLine { println(it) }
        }
    }
}