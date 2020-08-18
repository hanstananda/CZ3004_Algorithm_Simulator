import data.map.MazeMap
import utils.map.MapDescriptor
import utils.map.RandomMapGenerator

fun main() {
    println("Hello World!")
    val testMapDescriptor = MapDescriptor()
    var mazeMap = MazeMap()
//    testMapDescriptor.loadMapFromDisk(mazeMap, "/BlankMap.txt")
//    testMapDescriptor.debugMap(mazeMap)
    val testRandomMaze = RandomMapGenerator()
    mazeMap = testRandomMaze.createValidatedRandomMazeMap()
    testMapDescriptor.debugMap(mazeMap)

}