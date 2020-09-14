package data.robot

import com.google.gson.JsonObject
import constants.RobotConstants
import constants.RobotConstants.FRONT_SENSOR_IDS
import data.map.MazeMap
import mu.KotlinLogging

data class Sensor(
    val lowerRange: Int, val upperRange: Int,
    var row: Int, var col: Int, var dir: RobotConstants.DIRECTION, val id: String
) {
    private val logger = KotlinLogging.logger {}
    private val xSee = intArrayOf(0, 1, 0, -1)
    private val ySee = intArrayOf(1, 0, -1, 0)
    private var lastReading = -1

    fun setSensor(row: Int, col: Int, dir: RobotConstants.DIRECTION) {
        this.row = row
        this.col = col
        this.dir = dir
    }

    fun getJson(): JsonObject {
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
        logger.trace { "Sensor $id now located at ${dir.print()} ${ySee[dir.ordinal]} ${xSee[dir.ordinal]} simulating sense" }
        lastReading = -1
        // Check if starting point is valid for sensors with lowerRange > 1.
        for (i in 1 until lowerRange) {
            val rPos = this.row + ySee[dir.ordinal] * i
            val cPos = this.col + xSee[dir.ordinal] * i
            if (!realMap.checkValidCoordinates(rPos, cPos)) {
                lastReading = 0
                break
            }
            if (realMap.grid[rPos][cPos].obstacle) {
                lastReading = 0
                break
            }
        }

        if (lastReading != -1) {
            return lastReading
        }

        // Check if anything is detected by the sensor and return that value.
        for (i in lowerRange..upperRange) {
            val rPos = this.row + ySee[dir.ordinal] * i
            val cPos = this.col + xSee[dir.ordinal] * i
            if (!realMap.checkValidCoordinates(rPos, cPos)) {
                lastReading = i
                break
            }
            exploredMap.grid[rPos][cPos].explored = true

            if (realMap.grid[rPos][cPos].obstacle) {
                exploredMap.setObstacle(rPos, cPos, true)
                lastReading = i
                break
            }
        }
        return lastReading
    }

    /**
     * Sets the correct cells to explored and/or obstacle according to the actual sensor value.
     */
    fun processSensorVal(exploredMap: MazeMap, sensorVal: Int) {
        if (sensorVal == 0) return  // return value for LR sensor if obstacle before lowerRange

        // If above fails, check if starting point is valid for sensors with lowerRange > 1.
        for (i in 1 until lowerRange) {
            val row: Int = this.row + ySee[dir.ordinal] * i
            val col: Int = this.col + xSee[dir.ordinal] * i
            if (!exploredMap.checkValidCoordinates(row, col)) return
            if (exploredMap.grid[row][col].obstacle) return
        }

        // Update map according to sensor's value.
        for (i in lowerRange..upperRange) {
            val row: Int = this.row + ySee[dir.ordinal] * i
            val col: Int = this.col + xSee[dir.ordinal] * i
            if (!exploredMap.checkValidCoordinates(row, col)) continue
            exploredMap.grid[row][col].explored = true
            if (sensorVal == i) {
                exploredMap.setObstacle(row, col, true)
                break
            }

            // Override previous obstacle value if front sensors detect no obstacle.
            if (exploredMap.grid[row][col].obstacle) {
                if (id in FRONT_SENSOR_IDS) {
                    exploredMap.setObstacle(row, col, false)
                } else {
                    break
                }
            }
        }
    }
}