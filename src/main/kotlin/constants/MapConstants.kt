package constants

internal object MapConstants {
    const val DEFAULT_COL_SIZE = 15 // Total number of columns
    const val DEFAULT_ROW_SIZE = 20 // Total number of rows
    const val DEFAULT_MAP_SIZE = 300 // Total number of cells
    // Used in Map and possibly used in real run and simulator
    val POSSIBLEGRIDLABELS = arrayOf("Unexplored", "Explored", "Obstacle", "Waypoint", "Startpoint", "Endpoint")
    val UNEXPLORED = POSSIBLEGRIDLABELS[0]
    val EXPLORED = POSSIBLEGRIDLABELS[1]
    val OBSTACLE = POSSIBLEGRIDLABELS[2]
    val WAYPOINT = POSSIBLEGRIDLABELS[3]
    val STARTPOINT = POSSIBLEGRIDLABELS[4]
    val ENDPOINT = POSSIBLEGRIDLABELS[5]
}