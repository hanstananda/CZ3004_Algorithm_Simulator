package robot

import constants.DIRECTION
import constants.MOVEMENT
import constants.START_COL
import constants.START_ROW
import data.map.MazeMap
import data.robot.Robot
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.map.MapDescriptor

class RobotRunTests {
    private val mapDescriptor: MapDescriptor = MapDescriptor()
    private val testMap: MazeMap = MazeMap()
    private val filename =  "TestMap1.txt"

    @Test
    fun `check robot initialization success`() {
        Robot(START_ROW, START_COL)
    }

    @Test
    fun `check move forward working`() {
        val robot = Robot(START_ROW, START_COL)
        robot.robotDir = DIRECTION.NORTH
        robot.move(MOVEMENT.FORWARD)
        Assertions.assertEquals(DIRECTION.NORTH, robot.robotDir)
        Assertions.assertEquals(START_ROW+1, robot.row)
        Assertions.assertEquals(START_COL, robot.col)
    }

    @Test
    fun `check sensor simulation in robot working`() {
        TODO()
    }

    @BeforeEach
    fun initMap() {
        mapDescriptor.loadMapFromDisk(testMap, "/$filename")
    }

}