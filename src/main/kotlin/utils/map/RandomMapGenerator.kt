package utils.map

import data.map.MazeMap
import mu.KotlinLogging
import java.util.*
import kotlin.random.Random

class RandomMapGenerator {
    companion object {

        private val xMove = intArrayOf(-1, 0, 1, 0)
        private val yMove = intArrayOf(0, -1, 0, 1)
        private val logger = KotlinLogging.logger {}

        private fun createRandomMazeMap(wallPercentage: Int = 5): MazeMap {
            val mazeMap = MazeMap()
            for (row in 0 until mazeMap.rowSize) {
                for (col in 0 until mazeMap.colSize) {
                    if (Random.nextInt(100) < wallPercentage) {
                        mazeMap.setObstacle(row, col)
                    }
                }
            }
            return mazeMap
        }

        fun createValidatedRandomMazeMap(wallPercentage: Int = 5): MazeMap {
            var mazeMap: MazeMap
            var counter = 0
            do {
                mazeMap = createRandomMazeMap(wallPercentage)
                counter += 1
            } while (!validateMazeMap(mazeMap))
            logger.info { "Successfully created valid map after %d tries".format(counter) }
            return mazeMap
        }

        /**
         * Returns true if given map is valid
         * Uses flood-fill algorithm to determine whether every block is reachable
         */
        private fun validateMazeMap(mazeMap: MazeMap): Boolean {
            val debug: Boolean = logger.isDebugEnabled
            val startPoint = Pair(1, 1)
            val bfs: Queue<Pair<Int, Int>> = LinkedList()
            var validMaze = true
            val visited = Array(mazeMap.rowSize) { Array(mazeMap.colSize) { false } }
            bfs.add(startPoint)
            visited[startPoint.second][startPoint.first] = true
            while (!bfs.isEmpty()) {
                val curPos = bfs.remove()
                for (i in 0..3) {
                    val nextPos = Pair(curPos.first + xMove[i], curPos.second + yMove[i])
                    if (!(mazeMap.checkValidCoordinates(nextPos))) {
                        continue
                    } else if (visited[nextPos.second][nextPos.first] || mazeMap.grid[nextPos.second][nextPos.first].obstacle) {
                        continue
                    } else if (mazeMap.grid[nextPos.second][nextPos.first].virtualWall) {
                        visited[nextPos.second][nextPos.first] = true // Virtual wall, robot can map but cannot go through
                        continue
                    }
                    visited[nextPos.second][nextPos.first] = true
                    bfs.add(nextPos)
                    logger.debug { "Exploring %d,%d".format(nextPos.first, nextPos.second) }
                }
            }
            if (debug) {
                logger.debug { "Explored map:" }
                logger.debug { "Note: #= wall, 1= visited, 0 = not visited" }
                for (row in 0 until mazeMap.rowSize) {
                    val builder = StringBuilder()
                    for (col in 0 until mazeMap.colSize) {
                        if (mazeMap.grid[row][col].obstacle) {
                            builder.append("#")
                        } else if (visited[row][col]) {
                            builder.append("1")
                        } else {
                            builder.append("0")
                        }

                    }
                    logger.debug { builder.toString() }
                }
            }
            for (row in 0 until mazeMap.rowSize) {
                for (col in 0 until mazeMap.colSize) {
                    // Found a cell that cannot be mapped
                    if (!visited[row][col] and !mazeMap.grid[row][col].obstacle and !mazeMap.grid[row][col].virtualWall) {
                        logger.debug { "Maze invalid located at %d %d".format(row, col) }
                        validMaze = false
                        break
                    }
                }
                if (!validMaze) {
                    break
                }
            }
            return validMaze
        }
    }




}