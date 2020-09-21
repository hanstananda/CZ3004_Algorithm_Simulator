package data.robot

import constants.GraphicsConstants
import constants.MapConstants
import constants.RobotConstants
import data.map.MazeMap
import mu.KotlinLogging
import java.awt.Color
import java.awt.Graphics
import java.util.concurrent.TimeUnit
import javax.swing.JPanel

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

    var robotDir: RobotConstants.DIRECTION = RobotConstants.START_DIR
    var delay: Int = RobotConstants.DELAY
    var row: Int = startRow
    var col: Int = startCol
    private val logger = KotlinLogging.logger {}

    private val sensors: Array<Sensor> = arrayOf(
        // IR Short Range Front
        Sensor(
            RobotConstants.SENSOR_SHORT_RANGE_L, RobotConstants.SENSOR_SHORT_RANGE_H,
            row + 1, col - 1, robotDir, "IRS_FL"
        ),
        Sensor(
            RobotConstants.SENSOR_SHORT_RANGE_L, RobotConstants.SENSOR_SHORT_RANGE_H,
            row + 1, col, robotDir, "IRS_FM"
        ),
        Sensor(
            RobotConstants.SENSOR_SHORT_RANGE_L, RobotConstants.SENSOR_SHORT_RANGE_H,
            row + 1, col + 1, robotDir, "IRS_FR"
        ),
        // IR Short Range R
        Sensor(
            RobotConstants.SENSOR_SHORT_RANGE_L, RobotConstants.SENSOR_SHORT_RANGE_H,
            row + 1, col + 1, findNewDirection(RobotConstants.MOVEMENT.LEFT), "IRS_RF"
        ),
        Sensor(
            RobotConstants.SENSOR_SHORT_RANGE_L, RobotConstants.SENSOR_SHORT_RANGE_H,
            row - 1, col + 1, findNewDirection(RobotConstants.MOVEMENT.RIGHT), "IRS_RB"
        ),
        // IR Long Range L
        Sensor(
            RobotConstants.SENSOR_LONG_RANGE_L, RobotConstants.SENSOR_LONG_RANGE_H,
            row + 1, col - 1, findNewDirection(RobotConstants.MOVEMENT.LEFT), "IRL_LF"
        )
    )

    val sensorMap: MutableMap<String, Sensor> = sensors.map{ it.id to it }.toMap() as MutableMap<String, Sensor>

    private fun updateSensorPos() {
        when (robotDir) {
            RobotConstants.DIRECTION.NORTH -> {
                sensors[0].setSensor(row + 1, col - 1, robotDir)
                sensors[1].setSensor(row + 1, col, robotDir)
                sensors[2].setSensor(row + 1, col + 1, robotDir)

                sensors[3].setSensor(row + 1, col + 1, findNewDirection(RobotConstants.MOVEMENT.RIGHT))
                sensors[4].setSensor(row - 1, col + 1, findNewDirection(RobotConstants.MOVEMENT.RIGHT))

                sensors[5].setSensor(row + 1, col - 1, findNewDirection(RobotConstants.MOVEMENT.LEFT))
            }
            RobotConstants.DIRECTION.SOUTH -> {
                sensors[0].setSensor(row - 1, col + 1, robotDir)
                sensors[1].setSensor(row - 1, col, robotDir)
                sensors[2].setSensor(row - 1, col - 1, robotDir)

                sensors[3].setSensor(row - 1, col - 1, findNewDirection(RobotConstants.MOVEMENT.RIGHT))
                sensors[4].setSensor(row + 1, col - 1, findNewDirection(RobotConstants.MOVEMENT.RIGHT))

                sensors[5].setSensor(row - 1, col + 1, findNewDirection(RobotConstants.MOVEMENT.LEFT))
            }
            RobotConstants.DIRECTION.EAST -> {
                sensors[0].setSensor(row + 1, col + 1, robotDir)
                sensors[1].setSensor(row, col + 1, robotDir)
                sensors[2].setSensor(row - 1, col + 1, robotDir)

                sensors[3].setSensor(row - 1, col + 1, findNewDirection(RobotConstants.MOVEMENT.RIGHT))
                sensors[4].setSensor(row - 1, col - 1, findNewDirection(RobotConstants.MOVEMENT.RIGHT))

                sensors[5].setSensor(row + 1, col + 1, findNewDirection(RobotConstants.MOVEMENT.LEFT))

            }
            RobotConstants.DIRECTION.WEST -> {
                sensors[0].setSensor(row - 1, col - 1, robotDir)
                sensors[1].setSensor(row, col - 1, robotDir)
                sensors[2].setSensor(row + 1, col - 1, robotDir)

                sensors[3].setSensor(row + 1, col - 1, findNewDirection(RobotConstants.MOVEMENT.RIGHT))
                sensors[4].setSensor(row + 1, col + 1, findNewDirection(RobotConstants.MOVEMENT.RIGHT))

                sensors[5].setSensor(row - 1, col - 1, findNewDirection(RobotConstants.MOVEMENT.LEFT))

            }
        }
        for(sensor in sensors) {
            sensorMap[sensor.id] = sensor
        }
    }

    fun simulateSensors(exploredMap: MazeMap, realMap: MazeMap): Array<Int> {
        val res: Array<Int> = Array(sensors.size) { -1 }
        for (i in sensors.indices) {
            res[i] = sensors[i].simulateSense(exploredMap, realMap)
        }
        return res
    }

    fun getSensorReadings(exploredMap: MazeMap, realMap: MazeMap): Map<String, Int> {
        return sensors.associateBy({ it.id }, { it.simulateSense(exploredMap, realMap) })
    }

    fun resetRobot() {
        delay = RobotConstants.DELAY
        setRobotPosAndDir(startRow, startCol, RobotConstants.START_DIR)
    }

    fun setRobotPosAndDir(row: Int, col: Int, direction: RobotConstants.DIRECTION) {
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

    fun move(m: RobotConstants.MOVEMENT) {
        // Emulate real movement by pausing execution.
        try {
            TimeUnit.MILLISECONDS.sleep(delay.toLong())
        } catch (e: InterruptedException) {
            println("Something went wrong in Robot.move()!")
        }
        when (m) {
            RobotConstants.MOVEMENT.FORWARD -> when (robotDir) {
                RobotConstants.DIRECTION.NORTH -> row++
                RobotConstants.DIRECTION.EAST -> col++
                RobotConstants.DIRECTION.SOUTH -> row--
                RobotConstants.DIRECTION.WEST -> col--
            }
            RobotConstants.MOVEMENT.BACKWARD -> when (robotDir) {
                RobotConstants.DIRECTION.NORTH -> row--
                RobotConstants.DIRECTION.EAST -> col--
                RobotConstants.DIRECTION.SOUTH -> row++
                RobotConstants.DIRECTION.WEST -> col++
            }
            RobotConstants.MOVEMENT.LEFT, RobotConstants.MOVEMENT.RIGHT -> {
                robotDir = findNewDirection(m)
            }
        }
        logger.debug { "Current robot is located at ($row,$col) facing ${robotDir.print()}" }
        updateSensorPos()
    }

    private fun findNewDirection(m: RobotConstants.MOVEMENT): RobotConstants.DIRECTION {
        return if (m == RobotConstants.MOVEMENT.RIGHT) {
            RobotConstants.DIRECTION.getNext(robotDir)
        } else {
            RobotConstants.DIRECTION.getPrev(robotDir)
        }
    }

}