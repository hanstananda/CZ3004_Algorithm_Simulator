package utils.map

import constants.DEFAULT_COL_SIZE
import constants.DEFAULT_ROW_SIZE
import constants.DIRECTION
import data.map.MazeMap
import data.robot.Robot
import kotlin.math.abs

class MapDescriptor {
    fun loadMapFromDisk(mazeMap: MazeMap ,fileName: String) {
        MapDescriptor::class.java.getResourceAsStream(fileName).bufferedReader().useLines { it ->
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

    private fun binToHex(bin: String): String {
        val dec = bin.toInt(2)
        return Integer.toHexString(dec)
    }

    fun generateMapDescriptor(mazeMap: MazeMap): Array<String> {
        val ret = Array(2) {""}

        val Part1 = StringBuilder()
        val Part1_bin = StringBuilder()
        Part1_bin.append("11")
        for (r in 0 until DEFAULT_ROW_SIZE) {
            for (c in 0 until DEFAULT_COL_SIZE) {
                if (mazeMap.grid[r][c].explored) Part1_bin.append("1") else Part1_bin.append("0")
                if (Part1_bin.length == 4) {
                    Part1.append(binToHex(Part1_bin.toString()))
                    Part1_bin.setLength(0)
                }
            }
        }
        Part1_bin.append("11")
        Part1.append(binToHex(Part1_bin.toString()))
        println("P1: $Part1")
        ret[0] = Part1.toString()

        val Part2 = StringBuilder()
        val Part2_bin = StringBuilder()
        for (r in 0 until DEFAULT_ROW_SIZE) {
            for (c in 0 until DEFAULT_COL_SIZE) {
                if (mazeMap.grid[r][c].explored) {
                    if (mazeMap.grid[r][c].obstacle) Part2_bin.append("1") else Part2_bin.append("0")
                    if (Part2_bin.length == 4) {
                        Part2.append(binToHex(Part2_bin.toString()))
                        Part2_bin.setLength(0)
                    }
                }
            }
        }
        if (Part2_bin.isNotEmpty()) Part2.append(binToHex(Part2_bin.toString()))
        println("P2: $Part2")
        ret[1] = Part2.toString()

        return ret
    }


}