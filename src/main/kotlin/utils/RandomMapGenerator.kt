package utils

import data.map.MazeMap
import java.util.*
import kotlin.random.Random

class RandomMapGenerator {
    private val xMove = intArrayOf(-1,0,1,0)
    private val yMove = intArrayOf(0,-1,0,1)

    fun createRandomMazeMap(): MazeMap {
        val mazeMap = MazeMap()
        for (row in 0 until mazeMap.rowSize) {
            for (col in 0 until mazeMap.colSize) {
                mazeMap.setObstacle(row, col, Random.nextBoolean())
            }
        }
        return mazeMap
    }

    /**
     * Returns true if given map is valid
     * Uses flood-fill algorithm to determine whether every block is reachable
     */
    fun validateMazeMap(mazeMap: MazeMap): Boolean {
        val bfs: Queue<Pair<Int, Int>> = LinkedList()
        var validMaze = true
        val visited = Array(mazeMap.rowSize) { Array(mazeMap.colSize) { false } }
        bfs.add(Pair(0,0))
        visited[0][0]= true
        while(!bfs.isEmpty()) {
            val curPos = bfs.remove()
            for(i in 0..3) {
                val nextPos = Pair(curPos.first+ xMove[i], curPos.second + yMove[i])
                if (mazeMap.checkValidCoordinates(nextPos.second, nextPos.first) or
                    visited[nextPos.second][nextPos.first]) {
                    continue
                }
                else if(mazeMap.grid[nextPos.second][nextPos.first].virtualWall) {
                    visited[nextPos.second][nextPos.first] = true // Virtual wall, robot can map but cannot go through
                    continue
                }
                visited[nextPos.second][nextPos.first] = true
                bfs.add(nextPos)
            }
        }
        for (row in 0 until mazeMap.rowSize) {
            for (col in 0 until mazeMap.colSize) {
                // Found a cell that cannot be mapped
                if(!visited[row][col] and !mazeMap.grid[row][col].obstacle) {
                    println("Maze invalid located at %d %d".format(row, col))
                    validMaze = false
                }
            }
        }
        return validMaze
    }

}