package simulator

import constants.GraphicsConstants
import constants.MapConstants
import constants.RobotConstants
import data.map.MazeMap
import data.robot.Robot
import java.awt.Color
import java.awt.Graphics
import javax.swing.JPanel


class SimulatorMap(val map: MazeMap, val bot: Robot) : JPanel() {
    override fun paintComponent(g: Graphics) {
        // Create an array of displayCell objects for rendering.
        val mapCells = Array(map.rowSize) { mapRow ->
            Array(map.colSize) { mapCol ->
                DisplayCell(
                    mapCol * GraphicsConstants.CELL_SIZE,
                    mapRow * GraphicsConstants.CELL_SIZE,
                    GraphicsConstants.CELL_SIZE
                )
            }
        }
        // Clear previous paintings
        super.paintComponent(g);

        // Paint the cells with the appropriate colors.
        for (mapRow in 0 until MapConstants.DEFAULT_ROW_SIZE) {
            for (mapCol in 0 until MapConstants.DEFAULT_COL_SIZE) {
                val cellColor: Color =
                    when {
                        map.inStartZone(mapRow, mapCol) -> GraphicsConstants.START_CELL
                        map.inGoalZone(mapRow, mapCol) -> GraphicsConstants.GOAL_CELL
                        map.grid[mapRow][mapCol].obstacle -> GraphicsConstants.OBSTACLE_CELL
                        !map.grid[mapRow][mapCol].explored -> GraphicsConstants.UNEXPLORED_CELL
                        else -> GraphicsConstants.FREE_CELL
                    }
                g.color = cellColor
                g.fillRect(
                    mapCells[mapRow][mapCol].cellX + GraphicsConstants.MAP_X_OFFSET,
                    mapCells[mapRow][mapCol].cellY,
                    mapCells[mapRow][mapCol].cellSize,
                    mapCells[mapRow][mapCol].cellSize

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
        when (bot.robotDir) {
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


    private class DisplayCell(borderX: Int, borderY: Int, borderSize: Int) {
        val cellX: Int = borderX + GraphicsConstants.CELL_LINE_WEIGHT
        val cellY: Int = GraphicsConstants.MAP_H - (borderY - GraphicsConstants.CELL_LINE_WEIGHT)
        val cellSize: Int = borderSize - GraphicsConstants.CELL_LINE_WEIGHT * 2

    }
}