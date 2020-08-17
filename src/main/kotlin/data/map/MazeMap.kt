package data.map

import constants.DEFAULT_COL_SIZE
import constants.DEFAULT_ROW_SIZE

data class MazeMap(val rowSize: Int = DEFAULT_ROW_SIZE,val colSize: Int = DEFAULT_COL_SIZE) {
    val grid: Array<Array<Cell?>> = Array(rowSize) { arrayOfNulls<Cell>(colSize) }
    init {
        for (row in 0 until rowSize) {
            for( col in 0 until colSize) {
                grid[row][col] = Cell(row, col);
            }
        }
    }
    fun setAllUnexplored() {
        for (row in 0 until rowSize) {
            for( col in 0 until colSize) {
                grid[row][col]!!.explored = false;
            }
        }
    }
    fun setAllExplored() {
        for (row in 0 until rowSize) {
            for( col in 0 until colSize) {
                grid[row][col]!!.explored = true;
            }
        }
    }

    fun setIsObstacle(row: Int, col: Int, obstacle: Boolean) {
        grid[row][col]!!.obstacle = obstacle;
    }
}