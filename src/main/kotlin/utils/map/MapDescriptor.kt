package utils.map

import constants.RobotConstants
import data.map.MazeMap
import data.robot.Robot
import mu.KotlinLogging
import java.io.IOException
import kotlin.math.abs

val mapUtilsLogger = KotlinLogging.logger {}

fun loadMapFromDisk(mazeMap: MazeMap ,fileName: String) {
    try {
        mazeMap.reset()
        object {}.javaClass.getResourceAsStream("/mazemaps/$fileName.txt").bufferedReader().useLines { it ->
            it.forEachIndexed  { row, line ->
                for(col in 0 until mazeMap.colSize) {
                    if(line[col] == '1') {
                        mazeMap.setObstacle(mazeMap.rowSize-1-row, col, true)
                    }
                    if(line[col] in 'A'..'Z') {
                        mazeMap.grid[mazeMap.rowSize-1-row][col].phantomBlockInitial = line[col].toInt() - 'A'.toInt() + 1
                    }
                }
            }
            mazeMap.initExploredAreas()
        }
    } catch (e: NullPointerException) {
        e.printStackTrace()
    }
}

fun debugMap(mazeMap: MazeMap, robot: Robot? = null, usePrint: Boolean = false) {
    for (row in mazeMap.rowSize-1 downTo 0) {
        val builder = StringBuilder()
        for( col in 0 until mazeMap.colSize) {
            if(robot != null && abs(robot.row - row) <=1 && abs(robot.col - col) <=1) {
                if(robot.row==row && robot.col==col) {
                    when(robot.robotDir) {
                        RobotConstants.DIRECTION.NORTH -> {
                            builder.append("^")
                        }
                        RobotConstants.DIRECTION.EAST -> {
                            builder.append(">")
                        }
                        RobotConstants.DIRECTION.SOUTH -> {
                            builder.append("V")
                        }
                        RobotConstants.DIRECTION.WEST -> {
                            builder.append("<")
                        }
                    }
                }
                else {
                    builder.append("X")
                }

            }
            else if(mazeMap.grid[row][col].obstacle) {
                builder.append("#")
            }
            else if (mazeMap.grid[row][col].explored){
                builder.append("*")
            }
            else if(mazeMap.grid[row][col].virtualWall) {
                builder.append("|")
            }
            else {
                builder.append(".")
            }
        }
        if (usePrint) {
            println(builder.toString())
        }
        else {
            mapUtilsLogger.debug {builder.toString() }
        }

    }
}

fun generateMapDescriptor(mazeMap: MazeMap) {
    TODO()
}

