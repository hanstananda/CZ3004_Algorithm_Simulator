package robot

import constants.*
import constants.RobotConstants.SENSOR_SHORT_RANGE_H
import constants.RobotConstants.SENSOR_SHORT_RANGE_L
import constants.RobotConstants.START_COL
import constants.RobotConstants.START_ROW
import data.map.MazeMap
import data.robot.Sensor
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.map.loadMapFromDisk

class SensorSimulationTests {
    private val testMap: MazeMap = MazeMap()
    private val filename =  "TestMap1"

    @BeforeEach
    fun initMap() {
        loadMapFromDisk(testMap, filename)
    }

    @Test
    fun `check sensor initialization`() {
        Sensor(SENSOR_SHORT_RANGE_L, SENSOR_SHORT_RANGE_H, START_ROW, START_COL, RobotConstants.DIRECTION.NORTH, "IRS_TEST")
    }

    @Test
    fun `check sensor simulate sensing obstacle NORTH direction`() {
        val exploredMap = MazeMap()
        val sensor = Sensor(SENSOR_SHORT_RANGE_L, SENSOR_SHORT_RANGE_H, 0,0, RobotConstants.DIRECTION.NORTH, "IRS_TEST")
        // Current target obstacle from test map is at 2,7
        val obstacleRow = 2
        val obstacleCol = 7

        sensor.setSensor(obstacleRow-1,obstacleCol, RobotConstants.DIRECTION.NORTH)
        val res1 = sensor.simulateSense(exploredMap, testMap)
        Assertions.assertEquals(1, res1)
        Assertions.assertTrue(exploredMap.grid[obstacleRow][obstacleCol].obstacle)
        Assertions.assertTrue(exploredMap.grid[obstacleRow][obstacleCol].explored)
        Assertions.assertFalse(exploredMap.grid[obstacleRow+1][obstacleCol].explored)

        exploredMap.reset()
        sensor.setSensor(obstacleRow-2,obstacleCol, RobotConstants.DIRECTION.NORTH)
        val res2 = sensor.simulateSense(exploredMap, testMap)
        Assertions.assertEquals(2, res2)
        Assertions.assertTrue(exploredMap.grid[obstacleRow][obstacleCol].obstacle)
        Assertions.assertTrue(exploredMap.grid[obstacleRow][obstacleCol].explored)
        Assertions.assertTrue(exploredMap.grid[obstacleRow-1][obstacleCol].explored)

    }

    @Test
    fun `check sensor simulate sensing obstacle SOUTH direction`() {
        val exploredMap = MazeMap()
        val sensor = Sensor(SENSOR_SHORT_RANGE_L, SENSOR_SHORT_RANGE_H, 0,0, RobotConstants.DIRECTION.SOUTH, "IRS_TEST")
        val obstacleRow = 2
        val obstacleCol = 7

        sensor.setSensor(obstacleRow+1,obstacleCol, RobotConstants.DIRECTION.SOUTH)
        val res1 = sensor.simulateSense(exploredMap, testMap)
        Assertions.assertEquals(1, res1)
        Assertions.assertTrue(exploredMap.grid[obstacleRow][obstacleCol].obstacle)
        Assertions.assertTrue(exploredMap.grid[obstacleRow][obstacleCol].explored)
        Assertions.assertFalse(exploredMap.grid[obstacleRow-1][obstacleCol].explored)

        exploredMap.reset()
        sensor.setSensor(obstacleRow+2,obstacleCol, RobotConstants.DIRECTION.SOUTH)
        val res2 = sensor.simulateSense(exploredMap, testMap)
        Assertions.assertEquals(2, res2)
        Assertions.assertTrue(exploredMap.grid[obstacleRow][obstacleCol].obstacle)
        Assertions.assertTrue(exploredMap.grid[obstacleRow][obstacleCol].explored)
        Assertions.assertTrue(exploredMap.grid[obstacleRow+1][obstacleCol].explored)

    }

    @Test
    fun `check sensor simulate sensing obstacle WEST direction`() {
        val exploredMap = MazeMap()
        val sensor = Sensor(SENSOR_SHORT_RANGE_L, SENSOR_SHORT_RANGE_H, 0,0, RobotConstants.DIRECTION.WEST, "IRS_TEST")
        val obstacleRow = 2
        val obstacleCol = 7

        sensor.setSensor(obstacleRow,obstacleCol+1, RobotConstants.DIRECTION.WEST)
        val res1 = sensor.simulateSense(exploredMap, testMap)
        Assertions.assertEquals(1, res1)
        Assertions.assertTrue(exploredMap.grid[obstacleRow][obstacleCol].obstacle)
        Assertions.assertTrue(exploredMap.grid[obstacleRow][obstacleCol].explored)
        Assertions.assertFalse(exploredMap.grid[obstacleRow][obstacleCol-1].explored)

        exploredMap.reset()
        sensor.setSensor(obstacleRow,obstacleCol+2, RobotConstants.DIRECTION.WEST)
        val res2 = sensor.simulateSense(exploredMap, testMap)
        Assertions.assertEquals(2, res2)
        Assertions.assertTrue(exploredMap.grid[obstacleRow][obstacleCol].obstacle)
        Assertions.assertTrue(exploredMap.grid[obstacleRow][obstacleCol].explored)
        Assertions.assertTrue(exploredMap.grid[obstacleRow][obstacleCol+1].explored)
    }

    @Test
    fun `check sensor simulate sensing obstacle EAST direction`() {
        val exploredMap = MazeMap()
        val sensor = Sensor(SENSOR_SHORT_RANGE_L, SENSOR_SHORT_RANGE_H, 0,0, RobotConstants.DIRECTION.EAST, "IRS_TEST")
        val obstacleRow = 2
        val obstacleCol = 7

        sensor.setSensor(obstacleRow,obstacleCol-1, RobotConstants.DIRECTION.EAST)
        val res1 = sensor.simulateSense(exploredMap, testMap)
        Assertions.assertEquals(1, res1)
        Assertions.assertTrue(exploredMap.grid[obstacleRow][obstacleCol].obstacle)
        Assertions.assertTrue(exploredMap.grid[obstacleRow][obstacleCol].explored)
        Assertions.assertFalse(exploredMap.grid[obstacleRow][obstacleCol+1].explored)

        exploredMap.reset()
        sensor.setSensor(obstacleRow,obstacleCol-2, RobotConstants.DIRECTION.EAST)
        val res2 = sensor.simulateSense(exploredMap, testMap)
        Assertions.assertEquals(2, res2)
        Assertions.assertTrue(exploredMap.grid[obstacleRow][obstacleCol].obstacle)
        Assertions.assertTrue(exploredMap.grid[obstacleRow][obstacleCol].explored)
        Assertions.assertTrue(exploredMap.grid[obstacleRow][obstacleCol-1].explored)
    }
}