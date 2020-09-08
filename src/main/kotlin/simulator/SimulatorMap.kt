package simulator

import constants.GraphicsConstants
import constants.MapConstants
import constants.RobotConstants
import data.map.MazeMap
import data.robot.Robot
import java.awt.Color
import java.awt.Graphics
import javax.swing.JPanel

class SimulatorMap(var map: MazeMap, var bot: Robot) : JPanel() {
    override fun paintComponent(g: Graphics) {
        // Create an array of displayCell objects for rendering.
        val mapCells = Array(
                MapConstants.DEFAULT_ROW_SIZE
        ) { arrayOfNulls<displayCell>(MapConstants.DEFAULT_COL_SIZE) }
        for (mapRow in 0 until MapConstants.DEFAULT_ROW_SIZE) {
            for (mapCol in 0 until MapConstants.DEFAULT_COL_SIZE) {
                mapCells[mapRow][mapCol] = displayCell(
                        mapCol * GraphicsConstants.CELL_SIZE,
                        mapRow * GraphicsConstants.CELL_SIZE,
                        GraphicsConstants.CELL_SIZE
                )
            }
        }

        // Paint the cells with the appropriate colors.
        for (mapRow in 0 until MapConstants.DEFAULT_ROW_SIZE) {
            for (mapCol in 0 until MapConstants.DEFAULT_COL_SIZE) {
                var cellColor: Color
                cellColor = if (map.inStartZone(mapRow, mapCol)) GraphicsConstants.START_CELL else if (map.inGoalZone(
                                mapRow,
                                mapCol
                        )
                ) GraphicsConstants.GOAL_CELL else {
                    if (!map.grid[mapRow][mapCol].explored) GraphicsConstants.UNEXPLORED_CELL else if (map.grid[mapRow][mapCol]
                                    .obstacle
                    ) GraphicsConstants.OBSTACLE_CELL else GraphicsConstants.FREE_CELL
                }
                g.color = cellColor
                g.fillRect(
                        mapCells[mapRow][mapCol]!!.cellX + GraphicsConstants.MAP_X_OFFSET,
                        mapCells[mapRow][mapCol]!!.cellY,
                        mapCells[mapRow][mapCol]!!.cellSize,
                        mapCells[mapRow][mapCol]!!.cellSize
                )
            }
        }

        // Paint the robot on-screen.
        g.color = GraphicsConstants.ROBOT_CELL
        val r: Int = bot.row
        val c: Int = bot.col
        g.fillOval(
                (c - 1) * GraphicsConstants.CELL_SIZE + GraphicsConstants.ROBOT_X_OFFSET + GraphicsConstants.MAP_X_OFFSET,
                GraphicsConstants.MAP_H - (r * GraphicsConstants.CELL_SIZE + GraphicsConstants.ROBOT_Y_OFFSET),
                GraphicsConstants.ROBOT_W,
                GraphicsConstants.ROBOT_H
        )

        // Paint the robot's direction indicator on-screen.
        g.color = GraphicsConstants.ROBOT_DIR_CELL
        val d: RobotConstants.DIRECTION = bot.robotDir
        when(d) {
            RobotConstants.DIRECTION.NORTH -> g.fillOval(
                    c * GraphicsConstants.CELL_SIZE + 10 + GraphicsConstants.MAP_X_OFFSET,
                    GraphicsConstants.MAP_H - r * GraphicsConstants.CELL_SIZE - 15,
                    GraphicsConstants.ROBOT_DIR_W,
                    GraphicsConstants.ROBOT_DIR_H
            )
            RobotConstants.DIRECTION.EAST -> g.fillOval(
                    c * GraphicsConstants.CELL_SIZE + 35 + GraphicsConstants.MAP_X_OFFSET,
                    GraphicsConstants.MAP_H - r * GraphicsConstants.CELL_SIZE + 10,
                    GraphicsConstants.ROBOT_DIR_W,
                    GraphicsConstants.ROBOT_DIR_H
            )
            RobotConstants.DIRECTION.SOUTH -> g.fillOval(
                    c * GraphicsConstants.CELL_SIZE + 10 + GraphicsConstants.MAP_X_OFFSET,
                    GraphicsConstants.MAP_H - r * GraphicsConstants.CELL_SIZE + 35,
                    GraphicsConstants.ROBOT_DIR_W,
                    GraphicsConstants.ROBOT_DIR_H
            )
            RobotConstants.DIRECTION.WEST -> g.fillOval(
                    c * GraphicsConstants.CELL_SIZE - 15 + GraphicsConstants.MAP_X_OFFSET,
                    GraphicsConstants.MAP_H - r * GraphicsConstants.CELL_SIZE + 10,
                    GraphicsConstants.ROBOT_DIR_W,
                    GraphicsConstants.ROBOT_DIR_H
            )
        }
    }

    private class displayCell(borderX: Int, borderY: Int, borderSize: Int) {
        val cellX: Int
        val cellY: Int
        val cellSize: Int

        init {
            cellX = borderX + GraphicsConstants.CELL_LINE_WEIGHT
            cellY = GraphicsConstants.MAP_H - (borderY - GraphicsConstants.CELL_LINE_WEIGHT)
            cellSize = borderSize - GraphicsConstants.CELL_LINE_WEIGHT * 2
        }
    }
}