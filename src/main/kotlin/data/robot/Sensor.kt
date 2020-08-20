package data.robot

import constants.DIRECTION
import data.map.MazeMap
import mu.KotlinLogging

data class Sensor(
    val lowerRange: Int, val upperRange: Int,
    var row: Int, var col: Int, var dir: DIRECTION, val id: String
) {
    private val logger = KotlinLogging.logger {}
    private val xSee = intArrayOf(0,0,1,-1)
    private val ySee = intArrayOf(1,-1,0,0)

    fun setSensor(row: Int, col: Int, dir: DIRECTION) {
        this.row = row
        this.col = col
        this.dir = dir
    }

    /**
     * Check for obstacles and return the distance of the obstacle cell. Returns
     * -1 if no obstacle is detected.
     */
    fun simulateSense(exploredMap: MazeMap, realMap: MazeMap): Int {
        logger.debug{ "%d %d %d".format(dir.ordinal, ySee[dir.ordinal], xSee[dir.ordinal])}
        for(i in lowerRange..upperRange) {
            val rPos = this.row + ySee[dir.ordinal]*i
            val cPos = this.col + xSee[dir.ordinal]*i
            if(!realMap.checkValidCoordinates(rPos, cPos)) {
                return i
            }
            exploredMap.grid[rPos][cPos].explored = true

            if(realMap.grid[rPos][cPos].obstacle) {
                exploredMap.setObstacle(rPos, cPos)
                return i
            }
        }
        return -1
    }
}