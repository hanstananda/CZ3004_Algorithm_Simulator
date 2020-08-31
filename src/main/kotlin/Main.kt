import data.map.MazeMap
import utils.map.*
import utils.map.RandomMapGenerator

fun main() {
    println("Hello World!")
//    val testMapDescriptor = MapDescriptor()
    var mazeMap = MazeMap()
    loadMapFromDisk(mazeMap, "/BlankMap.txt")
    debugMap(mazeMap)
    val testRandomMaze = RandomMapGenerator()
    mazeMap = testRandomMaze.createValidatedRandomMazeMap()
//    testMapDescriptor.debugMap(mazeMap)

}