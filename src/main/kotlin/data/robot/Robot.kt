package data.robot

import constants.*
import data.map.MazeMap
import java.util.concurrent.TimeUnit

data class Robot(var startRow: Int, var startCol: Int) {
    /**
     * Represents the robot moving in the arena.
     *
     * The robot is represented by a 3 x 3 cell space as below:
     *
     *         ^   ^   ^
     *        IRS IRS IRS
     *   <IRL [X] [X] [X] IRS >
     *        [X] [X] [X]
     *        [X] [X] [X] IRS >
     *
     * IRS = Infrared Short Range Sensor, IRL = Infrared Long Range Sensor
     * IRS_FL = front-facing IRS positioned on the left
     * IRS_FM = front-facing IRS positioned on the middle
     * IRS_FR = front-facing IRS positioned on the right
     * IRS_RF = right-facing IRS positioned on the front
     * IRS_RB = right-facing IRS positioned on the back
     * IRL_LF = left-facing IRL positioned on the front
     *
     * @author Hans Tananda
     */

    var robotDir: DIRECTION = START_DIR
    var delay: Int = constants.DELAY
    var row: Int = startRow
    var col: Int = startCol

    private val sensors: Array<Sensor> = arrayOf(
        // IR Short Range Front
        Sensor(SENSOR_SHORT_RANGE_L, SENSOR_SHORT_RANGE_H,
            row+1, col-1, robotDir, "IRS_FL"),
        Sensor(SENSOR_SHORT_RANGE_L, SENSOR_SHORT_RANGE_H,
            row+1, col, robotDir, "IRS_FM"),
        Sensor(SENSOR_SHORT_RANGE_L, SENSOR_SHORT_RANGE_H,
            row+1, col+1, robotDir, "IRS_FR"),
        // IR Short Range R
        Sensor(SENSOR_SHORT_RANGE_L, SENSOR_SHORT_RANGE_H,
            row+1, col+1, findNewDirection(MOVEMENT.LEFT), "IRS_RF"),
        Sensor(SENSOR_SHORT_RANGE_L, SENSOR_SHORT_RANGE_H,
            row-1, col+1, findNewDirection(MOVEMENT.RIGHT), "IRS_RB"),
        // IR Long Range L
        Sensor(SENSOR_LONG_RANGE_L, SENSOR_LONG_RANGE_H,
            row+1, col-1, findNewDirection(MOVEMENT.LEFT), "IRL_LF")
    )

    private val sensorMap: Map<String, Sensor> = sensors.associateBy({it.id}, {it})

    private fun updateSensorPos() {
        when(robotDir) {
            DIRECTION.NORTH -> {
                sensors[0].setSensor(row+1, col-1, robotDir)
                sensors[1].setSensor(row+1, col, robotDir)
                sensors[2].setSensor(row+1, col+1, robotDir)

                sensors[3].setSensor(row+1, col+1,  findNewDirection(MOVEMENT.RIGHT))
                sensors[4].setSensor(row-1, col+1, findNewDirection(MOVEMENT.RIGHT))

                sensors[5].setSensor(row+1, col-1, findNewDirection(MOVEMENT.LEFT))
            }
            DIRECTION.SOUTH -> {
                sensors[0].setSensor(row-1, col+1, robotDir)
                sensors[1].setSensor(row-1, col, robotDir)
                sensors[2].setSensor(row-1, col-1, robotDir)

                sensors[3].setSensor(row-1, col-1,  findNewDirection(MOVEMENT.RIGHT))
                sensors[4].setSensor(row+1, col-1, findNewDirection(MOVEMENT.RIGHT))

                sensors[5].setSensor(row-1, col+1, findNewDirection(MOVEMENT.LEFT))
            }
            DIRECTION.EAST -> {
                sensors[0].setSensor(row-1, col+1, robotDir)
                sensors[1].setSensor(row, col+1, robotDir)
                sensors[2].setSensor(row+1, col+1, robotDir)

                sensors[3].setSensor(row-1, col+1,  findNewDirection(MOVEMENT.RIGHT))
                sensors[4].setSensor(row-1, col-1, findNewDirection(MOVEMENT.RIGHT))

                sensors[5].setSensor(row+1, col+1, findNewDirection(MOVEMENT.LEFT))

            }
            DIRECTION.WEST -> {
                sensors[0].setSensor(row-1, col-1, robotDir)
                sensors[1].setSensor(row, col-1, robotDir)
                sensors[2].setSensor(row+1, col-1, robotDir)

                sensors[3].setSensor(row+1, col-1,  findNewDirection(MOVEMENT.RIGHT))
                sensors[4].setSensor(row+1, col+1, findNewDirection(MOVEMENT.RIGHT))

                sensors[5].setSensor(row-1, col-1, findNewDirection(MOVEMENT.LEFT))

            }
        }
    }

    fun simulateSensors(exploredMap: MazeMap, realMap: MazeMap):Array<Int> {
        val res:Array<Int> = Array(sensors.size) {-1}
        for (i in sensors.indices) {
            res[i] = sensors[i].simulateSense(exploredMap, realMap)
        }
        return res
    }

    fun getSensorReadings(exploredMap: MazeMap, realMap: MazeMap): Map<String, Int> {
        return sensors.associateBy({it.id},{it.simulateSense(exploredMap, realMap) } )
    }

    fun resetRobot() {
        delay = DELAY
        setRobotPosAndDir(startRow, startCol, START_DIR)
    }

    fun setRobotPosAndDir(row: Int, col: Int, direction: DIRECTION) {
        this.row = row
        this.col = col
        robotDir = direction
        updateSensorPos()
    }

    fun setRobotPos(row: Int, col: Int) {
        this.row = row
        this.col = col
        updateSensorPos()
    }

    fun move(m: MOVEMENT) {
        // Emulate real movement by pausing execution.
        try {
            TimeUnit.MILLISECONDS.sleep(delay.toLong())
        } catch (e: InterruptedException) {
            println("Something went wrong in Robot.move()!")
        }
        when (m) {
            MOVEMENT.FORWARD -> when (robotDir) {
                DIRECTION.NORTH -> row++
                DIRECTION.EAST -> col++
                DIRECTION.SOUTH -> row--
                DIRECTION.WEST -> col--
            }
            MOVEMENT.BACKWARD -> when (robotDir) {
                DIRECTION.NORTH -> row--
                DIRECTION.EAST -> col--
                DIRECTION.SOUTH -> row++
                DIRECTION.WEST -> col++
            }
            MOVEMENT.LEFT, MOVEMENT.RIGHT -> {
                robotDir = findNewDirection(m)
            }
        }
        updateSensorPos()
    }

    private fun findNewDirection(m: MOVEMENT): DIRECTION {
        return if (m == MOVEMENT.RIGHT) {
            DIRECTION.getNext(robotDir)
        } else {
            DIRECTION.getPrev(robotDir)
        }
    }
}