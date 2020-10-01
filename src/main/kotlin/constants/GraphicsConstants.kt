package constants

import java.awt.Color

object GraphicsConstants {
    const val CELL_LINE_WEIGHT = 2
    val START_CELL: Color = Color.decode("#556F44")
    val GOAL_CELL: Color = Color.decode("#BA3B46")
    val UNEXPLORED_CELL: Color = Color.decode("#4A6C6F")
    val FREE_CELL: Color = Color.decode("#D7D9B1")
    val OBSTACLE_CELL: Color = Color.decode("#664147")
    val FASTEST_PATH_CELL: Color = Color.decode("#CB793A")
    val ROBOT_CELL: Color = Color.decode("#9B5DE5")
    val ROBOT_DIR_CELL: Color = Color.WHITE
    const val ROBOT_W = 70
    const val ROBOT_H = 70
    const val ROBOT_X_OFFSET = 10
    const val ROBOT_Y_OFFSET = 20
    const val ROBOT_DIR_W = 10
    const val ROBOT_DIR_H = 10
    const val CELL_SIZE = 30
    const val MAP_H = 600
    const val MAP_X_OFFSET = 120
}