package robot

import constants.DIRECTION
import constants.MOVEMENT
import constants.START_COL
import constants.START_ROW
import data.map.MazeMap
import data.robot.Robot
import org.junit.jupiter.api.*
import org.junit.jupiter.api.function.Executable
import utils.map.MapDescriptor

class RobotRunTests {
    private val mapDescriptor: MapDescriptor = MapDescriptor()
    private val testMap: MazeMap = MazeMap()
    private val filename = "TestMap1.txt"

    @Test
    fun `check robot initialization success`() {
        Robot(START_ROW, START_COL)
    }

    @Nested
    @DisplayName("Movement tests for robot simulation")
    inner class MovementTests {
        private val robot = Robot(START_ROW, START_COL)

        @BeforeEach
        fun `Initialize robot`() {
            robot.resetRobot()
        }

        @Test
        fun `check move forward working`() {
            robot.move(MOVEMENT.FORWARD)
            Assertions.assertEquals(DIRECTION.NORTH, robot.robotDir)
            Assertions.assertEquals(START_ROW + 1, robot.row)
            Assertions.assertEquals(START_COL, robot.col)
        }

        @Test
        fun `check move backward working`() {
            robot.setRobotPos(START_ROW + 1, START_COL)
            robot.move(MOVEMENT.BACKWARD)
            Assertions.assertEquals(DIRECTION.NORTH, robot.robotDir)
            Assertions.assertEquals(START_ROW, robot.row)
            Assertions.assertEquals(START_COL, robot.col)
        }

        @Test
        fun `check rotate right working`() {
            robot.move(MOVEMENT.RIGHT)
            Assertions.assertEquals(DIRECTION.EAST, robot.robotDir)
            Assertions.assertEquals(START_ROW, robot.row)
            Assertions.assertEquals(START_COL, robot.col)
        }

        @Test
        fun `check rotate left working`() {
            robot.move(MOVEMENT.LEFT)
            Assertions.assertEquals(DIRECTION.WEST, robot.robotDir)
            Assertions.assertEquals(START_ROW, robot.row)
            Assertions.assertEquals(START_COL, robot.col)
        }

        @Test
        fun `check forward left move loop back to position`() {
            for(i in 1..4) {
                robot.move(MOVEMENT.FORWARD)
                robot.move(MOVEMENT.LEFT)
            }
            Assertions.assertEquals(DIRECTION.NORTH, robot.robotDir)
            Assertions.assertEquals(START_ROW , robot.row)
            Assertions.assertEquals(START_COL, robot.col)
        }

        @Test
        fun `check right backward move loop back to position`() {
            for(i in 1..4) {
                robot.move(MOVEMENT.RIGHT)
                robot.move(MOVEMENT.BACKWARD)
            }
            Assertions.assertEquals(DIRECTION.NORTH, robot.robotDir)
            Assertions.assertEquals(START_ROW , robot.row)
            Assertions.assertEquals(START_COL, robot.col)
        }

    }

    @Nested
    @DisplayName("Sensors testing for robot simulation")
    inner class RobotSensorTests {
        private val robot = Robot(START_ROW, START_COL)
        private val exploredMap = MazeMap()

        @BeforeEach
        fun `Initialize robot`() {
            robot.resetRobot()
            exploredMap.reset()
        }

        @Test
        fun `check front sensor simulation in robot working`() {
            val obstacleRow = 8
            val obstacleColFr = 5
            val obstacleColTo = 7

            robot.setRobotPos(obstacleRow - 2, obstacleColFr)
            val ans1 = robot.simulateSensors(exploredMap, testMap)
            Assertions.assertEquals(-1, ans1[0])
            Assertions.assertEquals(1, ans1[1])
            Assertions.assertEquals(1, ans1[2])

            robot.setRobotPos(obstacleRow - 2, obstacleColFr + 1)
            val ans2 = robot.simulateSensors(exploredMap, testMap)
            Assertions.assertEquals(1, ans2[0])
            Assertions.assertEquals(1, ans2[1])
            Assertions.assertEquals(1, ans2[2])

            robot.setRobotPos(obstacleRow - 2, obstacleColTo)
            val ans3 = robot.simulateSensors(exploredMap, testMap)
            Assertions.assertEquals(1, ans3[0])
            Assertions.assertEquals(1, ans3[1])
            Assertions.assertEquals(-1, ans3[2])

        }

        @Test
        fun `sensing and rotating robot front sensor consistency check`() {
            val obstacleRow = 8
            val obstacleCol = 5

            robot.setRobotPos(obstacleRow - 2, obstacleCol)
            var ans = robot.simulateSensors(exploredMap, testMap)
            Assertions.assertEquals(-1, ans[0])
            Assertions.assertEquals(1, ans[1])
            Assertions.assertEquals(1, ans[2])

            robot.move(MOVEMENT.RIGHT)
            robot.move(MOVEMENT.LEFT)
            Assertions.assertAll(
                Executable { Assertions.assertEquals(DIRECTION.NORTH, robot.robotDir) },
                Executable { Assertions.assertEquals(obstacleRow - 2, robot.row) },
                Executable { Assertions.assertEquals(obstacleCol, robot.col) }
            )

            ans = robot.simulateSensors(exploredMap, testMap)
            Assertions.assertEquals(-1, ans[0])
            Assertions.assertEquals(1, ans[1])
            Assertions.assertEquals(1, ans[2])
        }

        @Test
        fun `sensing and moving robot front sensor consistency check`() {
            val obstacleRow = 8
            val obstacleCol = 5

            robot.setRobotPos(obstacleRow - 2, obstacleCol)
            var ans = robot.simulateSensors(exploredMap, testMap)
            Assertions.assertEquals(-1, ans[0])
            Assertions.assertEquals(1, ans[1])
            Assertions.assertEquals(1, ans[2])

            robot.move(MOVEMENT.RIGHT)
            robot.move(MOVEMENT.FORWARD)
            robot.move(MOVEMENT.LEFT)
            Assertions.assertAll(
                Executable { Assertions.assertEquals(DIRECTION.NORTH, robot.robotDir) },
                Executable { Assertions.assertEquals(obstacleRow - 2, robot.row) },
                Executable { Assertions.assertEquals(obstacleCol + 1, robot.col) }
            )

            ans = robot.simulateSensors(exploredMap, testMap)
            Assertions.assertEquals(1, ans[0])
            Assertions.assertEquals(1, ans[1])
            Assertions.assertEquals(1, ans[2])

        }
    }

    @BeforeEach
    fun initMap() {
        mapDescriptor.loadMapFromDisk(testMap, "/$filename")
    }

}
