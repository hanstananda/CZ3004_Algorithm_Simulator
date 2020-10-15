package data.map

import constants.MapConstants
import data.robot.Robot
import javax.swing.JPanel


// Note: Can consider removing the `data` in this class,
// as most data access are from method calls


data class MazeMap(val rowSize: Int = MapConstants.DEFAULT_ROW_SIZE, val colSize: Int = MapConstants.DEFAULT_COL_SIZE) {
    var grid: Array<Array<Cell>> = Array(rowSize) { i -> Array(colSize) { j -> Cell(i, j)} }
    private val xMove = intArrayOf(-1,0,1)
    private val yMove = intArrayOf(-1,0,1)

    init {
        reset()
    }

    fun reset() {
        resetAllObstacle()
        initExploredAreas()
        resetPhantomBlockInitialValues()
    }

    private fun resetPhantomBlockInitialValues() {
        for (row in 0 until rowSize) {
            for (col in 0 until colSize) {
                grid[row][col].phantomBlockInitial = 0
            }
        }
    }

    fun resetPhantomBlocksCount() {
        for (row in 0 until rowSize) {
            for (col in 0 until colSize) {
                grid[row][col].phantomBlock = grid[row][col].phantomBlockInitial
            }
        }
    }

    private fun resetAllObstacle() {
        for (row in 0 until rowSize) {
            for (col in 0 until colSize) {
                grid[row][col].obstacle = false
                // Set maze edges virtual wall to true, and the rest false
                grid[row][col].virtualWall = isVirtualMazeWall(row, col)
            }
        }

    }

    private fun isVirtualMazeWall(row: Int, col: Int):Boolean {
        if (row == 0 || row == rowSize - 1 ||
            col == 0 || col == colSize - 1
        ) {
            return true
        }
        return false
    }

    fun initExploredAreas() {
        for (row in 0 until rowSize) {
            for (col in 0 until colSize) {
                // Set start and goal explored areas to true, and the rest false
                grid[row][col].explored = inGoalZone(row, col) || inStartZone(row, col)
            }
        }
    }

    fun setAllExplored() {
        for (row in 0 until rowSize) {
            for (col in 0 until colSize) {
                grid[row][col].explored = true
            }
        }
    }

    fun setObstacle(row: Int, col: Int, obstacle: Boolean) {
        if (inStartZone(row, col) || inGoalZone(row, col)) {
            return
        }
        grid[row][col].obstacle = obstacle
        for(x in xMove) {
            for (y in yMove) {
                val rowT = row + y
                val colT = col + x
                if(checkValidCoordinates(rowT, colT)) {
                    if(!isVirtualMazeWall(rowT, colT)) {
                        //TODO: When removing virtual walls, must also check whether nearby still have active obstacle and decide accordingly
                        grid[rowT][colT].virtualWall = obstacle
                    }

                }
            }
        }
    }

    fun setWayPoint(row: Int, col: Int) {
        if (inStartZone(row, col) || inGoalZone(row, col)) {
            return
        }
        //TODO: error checking for waypoint?
        grid[row][col].waypoint = true
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
    fun inStartZone(row: Int, col: Int): Boolean {
        return row in 0..2 && col in 0..2
    }

    /**
     * Returns true if the row and column values are in the goal zone.
     */
    fun inGoalZone(row: Int, col: Int): Boolean {
        return row in rowSize - 3 until rowSize && col in colSize - 3 until colSize
    }

}