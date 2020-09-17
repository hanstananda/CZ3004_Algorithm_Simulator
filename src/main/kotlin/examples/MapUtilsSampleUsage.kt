package examples

import data.map.MazeMap
import mu.KotlinLogging
import utils.map.RandomMapGenerator
import utils.map.debugMap
import utils.map.loadMapFromDisk


fun main() {
    val logger = KotlinLogging.logger {}
    logger.info {"Test start main!"}
//    val testMapDescriptor = MapDescriptor()
    var mazeMap = MazeMap()
    loadMapFromDisk(mazeMap, "/TestMap1.txt")
    debugMap(mazeMap)
    mazeMap = RandomMapGenerator.createValidatedRandomMazeMap()
    debugMap(mazeMap)

}