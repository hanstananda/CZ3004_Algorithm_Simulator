package data.map

import constants.MapConstants
import constants.GraphicsConstants
import constants.RobotConstants
import data.robot.Robot
import java.awt.Color
import java.awt.Graphics
import javax.swing.JPanel

// Note: Can consider removing the `data` in this class,
// as most data access are from method calls

data class MazeMap(val rowSize: Int = MapConstants.DEFAULT_ROW_SIZE, val colSize: Int = MapConstants.DEFAULT_COL_SIZE) : JPanel() {
    var grid: Array<Array<Cell>> = Array(rowSize) { i -> Array(colSize) { j -> Cell(i, j)} }
    private var bot: Robot = Robot(1,1)
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
    fun inStartZone(row: Int, col: Int): Boolean {
        return row in 0..2 && col in 0..2
    }

    /**
     * Returns true if the row and column values are in the goal zone.
     */
    fun inGoalZone(row: Int, col: Int): Boolean {
        return row in rowSize - 3 until rowSize && col in colSize - 3 until colSize
    }

//    override fun paintComponent(g: Graphics) {
//        // Create an array of displayCell objects for rendering.
//        val mapCells = Array(
//                MapConstants.DEFAULT_ROW_SIZE
//        ) { arrayOfNulls<displayCell>(MapConstants.DEFAULT_COL_SIZE) }
//        for (mapRow in 0 until MapConstants.DEFAULT_ROW_SIZE) {
//            for (mapCol in 0 until MapConstants.DEFAULT_COL_SIZE) {
//                mapCells[mapRow][mapCol] = displayCell(
//                        mapCol * GraphicsConstants.CELL_SIZE,
//                        mapRow * GraphicsConstants.CELL_SIZE,
//                        GraphicsConstants.CELL_SIZE
//                )
//            }
//        }
//
//        // Paint the cells with the appropriate colors.
//        for (mapRow in 0 until MapConstants.DEFAULT_ROW_SIZE) {
//            for (mapCol in 0 until MapConstants.DEFAULT_COL_SIZE) {
//                var cellColor: Color
//                cellColor = if (inStartZone(mapRow, mapCol)) GraphicsConstants.START_CELL else if (inGoalZone(
//                                mapRow,
//                                mapCol
//                        )
//                ) GraphicsConstants.GOAL_CELL else {
//                    if (!grid[mapRow][mapCol].explored) GraphicsConstants.UNEXPLORED_CELL else if (grid[mapRow][mapCol]
//                                    .obstacle
//                    ) GraphicsConstants.OBSTACLE_CELL else GraphicsConstants.FREE_CELL
//                }
//                g.color = cellColor
//                g.fillRect(
//                        mapCells[mapRow][mapCol]!!.cellX + GraphicsConstants.MAP_X_OFFSET,
//                        mapCells[mapRow][mapCol]!!.cellY,
//                        mapCells[mapRow][mapCol]!!.cellSize,
//                        mapCells[mapRow][mapCol]!!.cellSize
//                )
//            }
//        }
//
//        // Paint the robot on-screen.
//        g.color = GraphicsConstants.ROBOT_CELL
//        val r: Int = bot.row
//        val c: Int = bot.col
//        g.fillOval(
//                (c - 1) * GraphicsConstants.CELL_SIZE + GraphicsConstants.ROBOT_X_OFFSET + GraphicsConstants.MAP_X_OFFSET,
//                GraphicsConstants.MAP_H - (r * GraphicsConstants.CELL_SIZE + GraphicsConstants.ROBOT_Y_OFFSET),
//                GraphicsConstants.ROBOT_W,
//                GraphicsConstants.ROBOT_H
//        )
//
//        // Paint the robot's direction indicator on-screen.
//        g.color = GraphicsConstants.ROBOT_DIR_CELL
//        val d: RobotConstants.DIRECTION = bot.robotDir
//        when (d) {
//            RobotConstants.DIRECTION.NORTH -> g.fillOval(
//                    c * GraphicsConstants.CELL_SIZE + 10 + GraphicsConstants.MAP_X_OFFSET,
//                    GraphicsConstants.MAP_H - r * GraphicsConstants.CELL_SIZE - 15,
//                    GraphicsConstants.ROBOT_DIR_W,
//                    GraphicsConstants.ROBOT_DIR_H
//            )
//            RobotConstants.DIRECTION.EAST -> g.fillOval(
//                    c * GraphicsConstants.CELL_SIZE + 35 + GraphicsConstants.MAP_X_OFFSET,
//                    GraphicsConstants.MAP_H - r * GraphicsConstants.CELL_SIZE + 10,
//                    GraphicsConstants.ROBOT_DIR_W,
//                    GraphicsConstants.ROBOT_DIR_H
//            )
//            RobotConstants.DIRECTION.SOUTH -> g.fillOval(
//                    c * GraphicsConstants.CELL_SIZE + 10 + GraphicsConstants.MAP_X_OFFSET,
//                    GraphicsConstants.MAP_H - r * GraphicsConstants.CELL_SIZE + 35,
//                    GraphicsConstants.ROBOT_DIR_W,
//                    GraphicsConstants.ROBOT_DIR_H
//            )
//            RobotConstants.DIRECTION.WEST -> g.fillOval(
//                    c * GraphicsConstants.CELL_SIZE - 15 + GraphicsConstants.MAP_X_OFFSET,
//                    GraphicsConstants.MAP_H - r * GraphicsConstants.CELL_SIZE + 10,
//                    GraphicsConstants.ROBOT_DIR_W,
//                    GraphicsConstants.ROBOT_DIR_H
//            )
//        }
//    }
//
//    private class displayCell(borderX: Int, borderY: Int, borderSize: Int) {
//        val cellX: Int
//        val cellY: Int
//        val cellSize: Int
//
//        init {
//            cellX = borderX + GraphicsConstants.CELL_LINE_WEIGHT
//            cellY = GraphicsConstants.MAP_H - (borderY - GraphicsConstants.CELL_LINE_WEIGHT)
//            cellSize = borderSize - GraphicsConstants.CELL_LINE_WEIGHT * 2
//        }
//    }

}