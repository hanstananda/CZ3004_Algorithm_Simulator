import data.map.MazeMap
import utils.MapDescriptor
import utils.RandomMapGenerator

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