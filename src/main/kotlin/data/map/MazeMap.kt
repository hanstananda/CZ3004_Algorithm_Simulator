package data.map

import constants.DEFAULT_COL_SIZE
import constants.DEFAULT_ROW_SIZE

// Note: Can consider removing the `data` in this class,
// as most data access are from method calls
data class MazeMap(val rowSize: Int = DEFAULT_ROW_SIZE, val colSize: Int = DEFAULT_COL_SIZE) {
    val grid: Array<Array<Cell>> = Array(rowSize) { i -> Array(colSize) { j -> Cell(i, j)} }
    private val xMove = intArrayOf(-1,0,1)
    private val yMove = intArrayOf(-1,0,1)

    init {
        for (row in 0 until rowSize) {
            for (col in 0 until colSize) {
                // Set the virtual walls of the maze
                if (row == 0 || row == rowSize - 1 ||
                    col == 0 || col == colSize - 1
                ) {
                    grid[row][col].virtualWall = true
                }
            }
        }
    }

    fun reset() {
        resetAllObstacle()
        setAllUnexplored()
    }

    fun resetAllObstacle() {
        for (row in 0 until rowSize) {
            for (col in 0 until colSize) {
                grid[row][col].obstacle = false;
                grid[row][col].virtualWall = false;
            }
        }
    }

    fun setAllUnexplored() {
        for (row in 0 until rowSize) {
            for (col in 0 until colSize) {
                grid[row][col].explored = false;
            }
        }
    }

    fun setAllExplored() {
        for (row in 0 until rowSize) {
            for (col in 0 until colSize) {
                grid[row][col].explored = true;
            }
        }
    }

    fun setObstacle(row: Int, col: Int) {
        if (inStartZone(row, col) || inGoalZone(row, col)) {
            return
        }
        grid[row][col].obstacle = true
        for(x in xMove) {
            for (y in yMove) {
                val rowT = row + y
                val colT = col + x
                if(checkValidCoordinates(rowT, colT)) {
                    //TODO: When removing virtual walls, must also check whether nearby still have active obstacle and decide accordingly
                    grid[rowT][colT].virtualWall = true;
                }
            }
        }
    }

    /**
     * Returns true if the row and column values are valid.
     */
    fun checkValidCoordinates(row: Int, col: Int): Boolean {
        return row in 0 until rowSize && col in 0 until colSize
    }

    /**
     * Returns true if the row and column values are valid.
     * Note: Pair is in form of (col, row) or (x,y)
     */
    fun checkValidCoordinates(coord: Pair<Int, Int>): Boolean {
        return checkValidCoordinates(coord.second, coord.first)
    }

    /**
     * Returns true if the row and column values are in the start zone.
     */
    private fun inStartZone(row: Int, col: Int): Boolean {
        return row in 0..2 && col in 0..2
    }

    /**
     * Returns true if the row and column values are in the goal zone.
     */
    private fun inGoalZone(row: Int, col: Int): Boolean {
        return row in rowSize - 3 until rowSize && col in colSize - 3 until colSize
    }
}