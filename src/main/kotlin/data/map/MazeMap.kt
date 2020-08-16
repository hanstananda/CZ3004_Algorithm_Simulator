package data.map

data class MazeMap(val rowSize: Int,val colSize: Int) {
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