package utils.map

import constants.RobotConstants
import data.map.MazeMap
import data.robot.Robot
import java.io.IOException
import kotlin.math.abs

fun loadMapFromDisk(mazeMap: MazeMap ,fileName: String) {
    try {
        object {}.javaClass.getResourceAsStream("/$fileName.txt").bufferedReader().useLines { it ->
            it.forEachIndexed  { row, line ->
                for(col in 0 until mazeMap.colSize) {
                    if(line[col] == '1') {
                        mazeMap.setObstacle(mazeMap.rowSize-1-row, col)
                    }
                }
            }
            mazeMap.setAllExplored()

        }
    } catch (e: IOException) {
        e.printStackTrace()
    }

}

fun debugMap(mazeMap: MazeMap, robot: Robot? = null) {
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
        println(builder.toString())
    }
}

fun generateMapDescriptor(mazeMap: MazeMap) {
    TODO()
}

