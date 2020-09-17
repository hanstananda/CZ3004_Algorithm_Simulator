package data.map

/**
 * This class is used to represent a cell in the map grid.
 * @property row the row value of this cell
 * @property col the column value of this cell
 * @author hanstananda
 */
data class Cell(val row: Int, val col: Int) {
    var obstacle: Boolean = false
    var explored: Boolean = false
    var virtualWall: Boolean = false
    var waypoint: Boolean = false
    var startpoint: Boolean = false
    var endpoint: Boolean = false

}