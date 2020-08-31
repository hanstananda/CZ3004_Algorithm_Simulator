package utils.map

import constants.DEFAULT_COL_SIZE
import constants.DIRECTION
import data.map.MazeMap
import data.robot.Robot
import kotlin.math.abs

fun loadMapFromDisk(mazeMap: MazeMap ,fileName: String) {
    object {}.javaClass.getResourceAsStream(fileName).bufferedReader().useLines { it ->
        it.forEachIndexed  { row, line ->
            for(col in 0 until DEFAULT_COL_SIZE) {
                if(line[col] == '1') {
                    mazeMap.setObstacle(mazeMap.rowSize-1-row, col)
                }
            }
        }
    }
}

fun debugMap(mazeMap: MazeMap, robot: Robot? = null) {
    for (row in mazeMap.rowSize-1 downTo 0) {
        val builder = StringBuilder()
        for( col in 0 until mazeMap.colSize) {
            if(robot != null && abs(robot.row - row) <=1 && abs(robot.col - col) <=1) {
                if(robot.row==row && robot.col==col) {
                    when(robot.robotDir) {
                        DIRECTION.NORTH -> {
                            builder.append("^")
                        }
                        DIRECTION.EAST -> {
                            builder.append(">")
                        }
                        DIRECTION.SOUTH -> {
                            builder.append("V")
                        }
                        DIRECTION.WEST -> {
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

