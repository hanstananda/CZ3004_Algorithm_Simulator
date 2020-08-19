package data.robot

import constants.DIRECTION
import data.map.MazeMap

data class Sensor(
    val lowerRange: Int, val upperRange: Int,
    var row: Int, var col: Int, var dir: DIRECTION, val id: String
) {
    private val xSee = intArrayOf(0,1,0,-1)
    private val ySee = intArrayOf(1,0,-1,0)

    fun setSensor(row: Int, col: Int, dir: DIRECTION) {
        this.row = row
        this.col = col
        this.dir = dir
    }

    /**
     * Check for obstacles and return the distance of the obstacle cell. Returns
     * -1 if no obstacle is detected.
     */
    fun sense(exploredMap: MazeMap,realMap: MazeMap): Int {
        for(i in lowerRange..upperRange) {
            val rPos = this.row + ySee[i]
            val cPos = this.col + xSee[i]
            if(!realMap.checkValidCoordinates(rPos, cPos)) {
                return i
            }
            exploredMap.grid[row][col].explored = true

            if(realMap.grid[row][col].obstacle) {
                exploredMap.setObstacle(row, col)
                return i
            }
        }
        return -1
    }
}