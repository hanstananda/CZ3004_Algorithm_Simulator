package robot

import constants.*
import data.map.MazeMap
import data.robot.Sensor
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.map.MapDescriptor

class SensorSimulationTests {
    private val mapDescriptor: MapDescriptor = MapDescriptor()
    private val testMap: MazeMap = MazeMap()
    private val filename =  "TestMap1.txt"

    @BeforeEach
    fun initMap() {
        mapDescriptor.loadMapFromDisk(testMap, "/$filename")
    }

    @Test
    fun `check sensor initialization`() {
        Sensor(SENSOR_SHORT_RANGE_L, SENSOR_SHORT_RANGE_H, START_ROW, START_COL, DIRECTION.NORTH, "IRS_TEST")
    }

    @Test
    fun `check sensor simulate sensing obstacle NORTH direction`() {
        var exploredMap = MazeMap()
        val sensor = Sensor(SENSOR_SHORT_RANGE_L, SENSOR_SHORT_RANGE_H, 0,0, DIRECTION.NORTH, "IRS_TEST")
        // Current target obstacle from test map is at 2,7
        val obstacleRow = 2
        val obstacleCol = 7

        sensor.setSensor(obstacleRow-1,obstacleCol, DIRECTION.NORTH)
        val res1 = sensor.simulateSense(exploredMap, testMap)
        Assertions.assertEquals(1, res1)
        Assertions.assertTrue(exploredMap.grid[obstacleRow][obstacleCol].obstacle)
        Assertions.assertTrue(exploredMap.grid[obstacleRow][obstacleCol].explored)
        Assertions.assertFalse(exploredMap.grid[obstacleRow+1][obstacleCol].explored)

        exploredMap = MazeMap()
        sensor.setSensor(obstacleRow-2,obstacleCol, DIRECTION.NORTH)
        val res2 = sensor.simulateSense(exploredMap, testMap)
        Assertions.assertEquals(2, res2)
        Assertions.assertTrue(exploredMap.grid[obstacleRow][obstacleCol].obstacle)
        Assertions.assertTrue(exploredMap.grid[obstacleRow][obstacleCol].explored)
        Assertions.assertTrue(exploredMap.grid[obstacleRow-1][obstacleCol].explored)

    }

    @Test
    fun `check sensor simulate sensing obstacle SOUTH direction`() {
        var exploredMap = MazeMap()
        val sensor = Sensor(SENSOR_SHORT_RANGE_L, SENSOR_SHORT_RANGE_H, 0,0, DIRECTION.SOUTH, "IRS_TEST")
        val obstacleRow = 2
        val obstacleCol = 7

        sensor.setSensor(obstacleRow+1,obstacleCol, DIRECTION.SOUTH)
        val res1 = sensor.simulateSense(exploredMap, testMap)
        Assertions.assertEquals(1, res1)
        Assertions.assertTrue(exploredMap.grid[obstacleRow][obstacleCol].obstacle)
        Assertions.assertTrue(exploredMap.grid[obstacleRow][obstacleCol].explored)
        Assertions.assertFalse(exploredMap.grid[obstacleRow-1][obstacleCol].explored)

        exploredMap = MazeMap()
        sensor.setSensor(obstacleRow+2,obstacleCol, DIRECTION.SOUTH)
        val res2 = sensor.simulateSense(exploredMap, testMap)
        Assertions.assertEquals(2, res2)
        Assertions.assertTrue(exploredMap.grid[obstacleRow][obstacleCol].obstacle)
        Assertions.assertTrue(exploredMap.grid[obstacleRow][obstacleCol].explored)
        Assertions.assertTrue(exploredMap.grid[obstacleRow+1][obstacleCol].explored)

    }

    @Test
    fun `check sensor simulate sensing obstacle WEST direction`() {
        var exploredMap = MazeMap()
        val sensor = Sensor(SENSOR_SHORT_RANGE_L, SENSOR_SHORT_RANGE_H, 0,0, DIRECTION.WEST, "IRS_TEST")
        val obstacleRow = 2
        val obstacleCol = 7

        sensor.setSensor(obstacleRow,obstacleCol+1, DIRECTION.WEST)
        val res1 = sensor.simulateSense(exploredMap, testMap)
        Assertions.assertEquals(1, res1)
        Assertions.assertTrue(exploredMap.grid[obstacleRow][obstacleCol].obstacle)
        Assertions.assertTrue(exploredMap.grid[obstacleRow][obstacleCol].explored)
        Assertions.assertFalse(exploredMap.grid[obstacleRow][obstacleCol-1].explored)

        exploredMap = MazeMap()
        sensor.setSensor(obstacleRow,obstacleCol+2, DIRECTION.WEST)
        val res2 = sensor.simulateSense(exploredMap, testMap)
        Assertions.assertEquals(2, res2)
        Assertions.assertTrue(exploredMap.grid[obstacleRow][obstacleCol].obstacle)
        Assertions.assertTrue(exploredMap.grid[obstacleRow][obstacleCol].explored)
        Assertions.assertTrue(exploredMap.grid[obstacleRow][obstacleCol+1].explored)
    }

    @Test
    fun `check sensor simulate sensing obstacle EAST direction`() {
        var exploredMap = MazeMap()
        val sensor = Sensor(SENSOR_SHORT_RANGE_L, SENSOR_SHORT_RANGE_H, 0,0, DIRECTION.EAST, "IRS_TEST")
        val obstacleRow = 2
        val obstacleCol = 7

        sensor.setSensor(obstacleRow,obstacleCol-1, DIRECTION.EAST)
        val res1 = sensor.simulateSense(exploredMap, testMap)
        Assertions.assertEquals(1, res1)
        Assertions.assertTrue(exploredMap.grid[obstacleRow][obstacleCol].obstacle)
        Assertions.assertTrue(exploredMap.grid[obstacleRow][obstacleCol].explored)
        Assertions.assertFalse(exploredMap.grid[obstacleRow][obstacleCol+1].explored)

        exploredMap = MazeMap()
        sensor.setSensor(obstacleRow,obstacleCol-2, DIRECTION.EAST)
        val res2 = sensor.simulateSense(exploredMap, testMap)
        Assertions.assertEquals(2, res2)
        Assertions.assertTrue(exploredMap.grid[obstacleRow][obstacleCol].obstacle)
        Assertions.assertTrue(exploredMap.grid[obstacleRow][obstacleCol].explored)
        Assertions.assertTrue(exploredMap.grid[obstacleRow][obstacleCol-1].explored)
    }
}