package utils

import constants.DEFAULT_COL_SIZE
import data.map.Cell
import data.map.MazeMap

class MapDescriptor {
    fun loadMapFromDisk(mazeMap: MazeMap ,fileName: String) {
        MapDescriptor::class.java.getResourceAsStream(fileName).bufferedReader().useLines { it ->
            it.forEachIndexed  { row, line ->
                for(col in 0 until DEFAULT_COL_SIZE) {
                    mazeMap.setObstacle(row, col, line[col] == '1')
                }
            }
        }
    }

    fun debugMap(mazeMap: MazeMap) {
        for (row in 0 until mazeMap.rowSize) {
            val builder = StringBuilder()
            for( col in 0 until mazeMap.colSize) {
                builder.append(if(mazeMap.grid[row][col].obstacle) "1" else "0")
            }
            println(builder.toString())
        }
    }

    fun generateMapDescriptor(mazeMap: MazeMap) {

    }


}