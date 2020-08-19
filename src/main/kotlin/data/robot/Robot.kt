package data.robot

import constants.*
import java.util.*
import java.util.concurrent.TimeUnit

data class Robot(var row: Int, var col: Int) {
    var robotDir: DIRECTION = START_DIR
    var delay: Int = constants.DELAY

    // TODO: Finalize sensor placements and add ultrasonic sensors
    val sensors: Array<Sensor> = arrayOf(
        Sensor(SENSOR_SHORT_RANGE_L, SENSOR_SHORT_RANGE_H,
        row+1, col-1, robotDir, "IRS_FL"),
        Sensor(SENSOR_SHORT_RANGE_L, SENSOR_SHORT_RANGE_H,
            row+1, col, robotDir, "IRS_FM"),
        Sensor(SENSOR_SHORT_RANGE_L, SENSOR_SHORT_RANGE_H,
            row+1, col+1, robotDir, "IRS_FR"),
        Sensor(SENSOR_SHORT_RANGE_L, SENSOR_SHORT_RANGE_H,
            row, col-1, findNewDirection(MOVEMENT.LEFT), "IRS_L"),
        Sensor(SENSOR_SHORT_RANGE_L, SENSOR_SHORT_RANGE_H,
            row, col+1, findNewDirection(MOVEMENT.RIGHT), "IRS_R"),
        Sensor(SENSOR_LONG_RANGE_L, SENSOR_LONG_RANGE_H,
            row-1, col+1, findNewDirection(MOVEMENT.LEFT), "IRL_L")
        )

    private fun updateSensorPos() {
        when(robotDir) {
            DIRECTION.NORTH -> {
                sensors[0].setSensor(row+1, col-1, robotDir)
                sensors[1].setSensor(row+1, col, robotDir)
                sensors[2].setSensor(row+1, col+1, robotDir)

                sensors[3].setSensor(row, col-1,  findNewDirection(MOVEMENT.LEFT))
                sensors[4].setSensor(row, col+1, findNewDirection(MOVEMENT.RIGHT))

                sensors[5].setSensor(row-1, col+1, findNewDirection(MOVEMENT.LEFT))
            }
            DIRECTION.SOUTH -> {
                sensors[0].setSensor(row-1, col+1, robotDir)
                sensors[1].setSensor(row-1, col, robotDir)
                sensors[2].setSensor(row-1, col-1, robotDir)

                sensors[3].setSensor(row, col+1,  findNewDirection(MOVEMENT.LEFT))
                sensors[4].setSensor(row, col-1, findNewDirection(MOVEMENT.RIGHT))

                sensors[5].setSensor(row+1, col-1, findNewDirection(MOVEMENT.LEFT))
            }
            DIRECTION.EAST -> {
                sensors[0].setSensor(row-1, col+1, robotDir)
                sensors[1].setSensor(row, col+1, robotDir)
                sensors[2].setSensor(row+1, col+1, robotDir)

                sensors[3].setSensor(row+1, col,  findNewDirection(MOVEMENT.LEFT))
                sensors[4].setSensor(row-1, col, findNewDirection(MOVEMENT.RIGHT))

                sensors[5].setSensor(row-1, col+1, findNewDirection(MOVEMENT.LEFT))

            }
            DIRECTION.WEST -> {
                sensors[0].setSensor(row-1, col-1, robotDir)
                sensors[1].setSensor(row, col-1, robotDir)
                sensors[2].setSensor(row+1, col-1, robotDir)

                sensors[3].setSensor(row-1, col,  findNewDirection(MOVEMENT.LEFT))
                sensors[4].setSensor(row+1, col, findNewDirection(MOVEMENT.RIGHT))

                sensors[5].setSensor(row+1, col-1, findNewDirection(MOVEMENT.LEFT))

            }
        }
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
        when(m) {
            MOVEMENT.FORWARD -> when(robotDir) {
                DIRECTION.NORTH -> row++
                DIRECTION.EAST -> col++
                DIRECTION.SOUTH -> row--
                DIRECTION.WEST -> col--
            }
            MOVEMENT.BACKWARD -> when(robotDir) {
                DIRECTION.NORTH -> row--
                DIRECTION.EAST -> col--
                DIRECTION.SOUTH -> row++
                DIRECTION.WEST -> col++
            }
            MOVEMENT.LEFT, MOVEMENT.RIGHT -> {
                findNewDirection(m)
            }
        }
    }

    private fun findNewDirection(m: MOVEMENT): DIRECTION {
        return if (m == MOVEMENT.RIGHT) {
            DIRECTION.getNext(robotDir)
        } else {
            DIRECTION.getPrev(robotDir)
        }
    }
}