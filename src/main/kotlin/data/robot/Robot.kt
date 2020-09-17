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

data class Robot(var startRow: Int, var startCol: Int) : JPanel() {
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
                sensors[0].setSensor(row - 1, col + 1, robotDir)
                sensors[1].setSensor(row, col + 1, robotDir)
                sensors[2].setSensor(row + 1, col + 1, robotDir)

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
        row = checkValidRow(row)
        col = checkValidCol(col)
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
        row = checkValidRow(row)
        col = checkValidCol(col)
    }

    private fun findNewDirection(m: RobotConstants.MOVEMENT): RobotConstants.DIRECTION {
        return if (m == RobotConstants.MOVEMENT.RIGHT) {
            RobotConstants.DIRECTION.getNext(robotDir)
        } else {
            RobotConstants.DIRECTION.getPrev(robotDir)
        }
    }

    private fun checkValidRow(x: Int): Int {
        var x = x
        if (x >= MapConstants.DEFAULT_ROW_SIZE - 2) {
            x = MapConstants.DEFAULT_ROW_SIZE - 2
        }
        if (x <= 0) {
            x = 1
        }
        return x
    }

    private fun checkValidCol(y: Int): Int {
        var y = y
        if (y >= MapConstants.DEFAULT_COL_SIZE - 2) {
            y = MapConstants.DEFAULT_COL_SIZE - 2
        }
        if (y <= 0) {
            y = 1
        }
        return y
    }

    override fun paintComponent(g: Graphics) {
        // Paint the robot on-screen.
        g.color = GraphicsConstants.ROBOT_CELL
        val r: Int = this.row
        val c: Int = this.col
        g.fillOval(
                (c - 1) * GraphicsConstants.CELL_SIZE + GraphicsConstants.ROBOT_X_OFFSET + GraphicsConstants.MAP_X_OFFSET,
                GraphicsConstants.MAP_H - (r * GraphicsConstants.CELL_SIZE + GraphicsConstants.ROBOT_Y_OFFSET),
                GraphicsConstants.ROBOT_W,
                GraphicsConstants.ROBOT_H
        )

        // Paint the robot's direction indicator on-screen.
        g.color = GraphicsConstants.ROBOT_DIR_CELL
        val d: RobotConstants.DIRECTION = this.robotDir
        when (d) {
            RobotConstants.DIRECTION.NORTH -> g.fillOval(
                    c * GraphicsConstants.CELL_SIZE + 10 + GraphicsConstants.MAP_X_OFFSET,
                    GraphicsConstants.MAP_H - r * GraphicsConstants.CELL_SIZE - 15,
                    GraphicsConstants.ROBOT_DIR_W,
                    GraphicsConstants.ROBOT_DIR_H
            )
            RobotConstants.DIRECTION.EAST -> g.fillOval(
                    c * GraphicsConstants.CELL_SIZE + 35 + GraphicsConstants.MAP_X_OFFSET,
                    GraphicsConstants.MAP_H - r * GraphicsConstants.CELL_SIZE + 10,
                    GraphicsConstants.ROBOT_DIR_W,
                    GraphicsConstants.ROBOT_DIR_H
            )
            RobotConstants.DIRECTION.SOUTH -> g.fillOval(
                    c * GraphicsConstants.CELL_SIZE + 10 + GraphicsConstants.MAP_X_OFFSET,
                    GraphicsConstants.MAP_H - r * GraphicsConstants.CELL_SIZE + 35,
                    GraphicsConstants.ROBOT_DIR_W,
                    GraphicsConstants.ROBOT_DIR_H
            )
            RobotConstants.DIRECTION.WEST -> g.fillOval(
                    c * GraphicsConstants.CELL_SIZE - 15 + GraphicsConstants.MAP_X_OFFSET,
                    GraphicsConstants.MAP_H - r * GraphicsConstants.CELL_SIZE + 10,
                    GraphicsConstants.ROBOT_DIR_W,
                    GraphicsConstants.ROBOT_DIR_H
            )
        }
    }

}