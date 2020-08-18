package utils.map

import constants.DEFAULT_COL_SIZE
import data.map.MazeMap

class MapDescriptor {
    fun loadMapFromDisk(mazeMap: MazeMap ,fileName: String) {
        MapDescriptor::class.java.getResourceAsStream(fileName).bufferedReader().useLines { it ->
            it.forEachIndexed  { row, line ->
                for(col in 0 until DEFAULT_COL_SIZE) {
                    if(line[col] == '1') {
                        mazeMap.setObstacle(row, col)
                    }
                }
            }
        }
    }

    fun debugMap(mazeMap: MazeMap) {
        for (row in 0 until mazeMap.rowSize) {
            val builder = StringBuilder()
            for( col in 0 until mazeMap.colSize) {
                if(mazeMap.grid[row][col].obstacle) {
                    builder.append("#")
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

    }


}