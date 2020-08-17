import data.map.MazeMap
import utils.MapDescriptor

fun main() {
    println("Hello World!")
    val test = MapDescriptor()
    val mazeMap = MazeMap()
    test.loadMapFromDisk(mazeMap, "/BlankMap.txt")
    test.debugMap(mazeMap)
}