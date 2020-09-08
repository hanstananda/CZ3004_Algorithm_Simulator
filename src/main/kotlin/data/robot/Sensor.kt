package data.robot

import constants.RobotConstants
import com.google.gson.JsonObject
import data.map.MazeMap
import mu.KotlinLogging

data class Sensor(
    val lowerRange: Int, val upperRange: Int,
    var row: Int, var col: Int, var dir: RobotConstants.DIRECTION, val id: String
) {
    private val logger = KotlinLogging.logger {}
    private val xSee = intArrayOf(0,1,0,-1)
    private val ySee = intArrayOf(1,0,-1,0)
    private var lastReading = -1

    fun setSensor(row: Int, col: Int, dir: RobotConstants.DIRECTION) {
        this.row = row
        this.col = col
        this.dir = dir
    }

    fun getJson():JsonObject {
        val json = JsonObject()
        json.addProperty("update", "sensor_read")
        json.addProperty("value", lastReading)
        json.addProperty("id", id)
        return json
    }

    /**
     * Check for obstacles and return the distance of the obstacle cell. Returns
     * -1 if no obstacle is detected.
     */
    fun simulateSense(exploredMap: MazeMap, realMap: MazeMap): Int {
        logger.debug{"Sensor $id now located at ${dir.print()} ${ySee[dir.ordinal]} ${xSee[dir.ordinal]} simulating sense" }
        lastReading = -1
        for(i in 1 until lowerRange) {
            val rPos = this.row + ySee[dir.ordinal]*i
            val cPos = this.col + xSee[dir.ordinal]*i
            if(!realMap.checkValidCoordinates(rPos, cPos)) {
                lastReading = i
                break
            }
            if(realMap.grid[rPos][cPos].obstacle) {
                lastReading = i
                break
            }
        }

        if(lastReading!=-1) {
            return lastReading
        }

        for(i in lowerRange..upperRange) {
            val rPos = this.row + ySee[dir.ordinal]*i
            val cPos = this.col + xSee[dir.ordinal]*i
            if(!realMap.checkValidCoordinates(rPos, cPos)) {
                lastReading = i
                break
            }
            exploredMap.grid[rPos][cPos].explored = true

            if(realMap.grid[rPos][cPos].obstacle) {
                exploredMap.setObstacle(rPos, cPos)
                lastReading = i
                break
            }
        }
        return lastReading
    }
}