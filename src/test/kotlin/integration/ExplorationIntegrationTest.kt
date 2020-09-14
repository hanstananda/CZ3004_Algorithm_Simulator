package integration

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import constants.CommConstants
import constants.RobotConstants
import constants.RobotConstants.START_COL
import constants.RobotConstants.START_ROW
import data.map.MazeMap
import data.robot.Robot
import io.ktor.client.*
import io.ktor.client.features.websocket.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import io.ktor.util.*
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.junit.jupiter.api.Test
import simulator.Simulator.logger
import utils.map.debugMap
import java.util.HashMap
import java.util.concurrent.TimeUnit

@KtorExperimentalAPI
class ExplorationIntegrationTest {
    private val logger = KotlinLogging.logger {}
    lateinit var session: WebSocketSession

    private val client = HttpClient {
        install(WebSockets)
    }

    @Test
    fun explorationTest() {
        runBlocking {
            client.ws(
                method = HttpMethod.Get,
                host = "127.0.0.1",
                port = 8080, path = "/robot"
            ) { // this: DefaultClientWebSocketSession
                session = this

                // Send text frame.
                send("/force_reset")
                send("/force_start_exploration")

                // Receive frame.
                when (val frame = incoming.receive()) {
                    is Frame.Text -> {
                        val reply = frame.readText()
                        logger.info { reply }
                    }
//                    is Frame.Binary -> println(frame.readBytes())
                }

                val run = Exploration(this, START_ROW, START_COL )
                run.startCalibration()
                run.explorationLoop()
                client.close()
            }
        }
    }

    @KtorExperimentalAPI
    class Exploration(
        private val commSession: WebSocketSession,
        private val startRow: Int,
        private val startCol: Int,
        private val coverageLimit: Int = 100,
        timeLimit: Long = 60 * 5,
    ) {

        private val bot: Robot = Robot(startRow, startCol)
        private val endTime = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(timeLimit)
        private var areaExplored = 0
        private var exploredMap = MazeMap()

        fun startCalibration() {
            moveBot(RobotConstants.MOVEMENT.RIGHT)
            moveBot(RobotConstants.MOVEMENT.RIGHT)
            moveBot(RobotConstants.MOVEMENT.LEFT)
            moveBot(RobotConstants.MOVEMENT.LEFT)
        }

        fun explorationLoop() {
            var counter = 0
            do {
                nextMove()
                areaExplored = calculateAreaExplored()
                logger.debug { "Area explored: $areaExplored" }
                if (bot.row == startRow && bot.col == startCol) {
                    if (areaExplored >= coverageLimit) {
                        break
                    }
                }
                counter+=1
                if(counter>10) {
                    break
                }
            } while (areaExplored <= coverageLimit && System.currentTimeMillis() <= endTime)
//        goHome()
        }

        private fun calculateAreaExplored(): Int {
            var result = 0
            for (r in 0 until exploredMap.rowSize) {
                for (c in 0 until exploredMap.colSize) {
                    if (exploredMap.grid[r][c].explored) {
                        result++
                    }
                }
            }
            return result
        }

        private fun nextMove() {
            logger.debug { " ${lookFree(RobotConstants.DIRECTION.getNext(bot.robotDir))} ${lookFree(bot.robotDir)} ${lookFree(RobotConstants.DIRECTION.getPrev(bot.robotDir))}"}

            when {
                lookFree(RobotConstants.DIRECTION.getNext(bot.robotDir)) -> {
                    moveBot(RobotConstants.MOVEMENT.RIGHT)
                    if (lookFree(bot.robotDir)) moveBot(RobotConstants.MOVEMENT.FORWARD)
                }
                lookFree(bot.robotDir) -> {
                    moveBot(RobotConstants.MOVEMENT.FORWARD)
                }
                lookFree(RobotConstants.DIRECTION.getPrev(bot.robotDir)) -> {
                    moveBot(RobotConstants.MOVEMENT.LEFT)
                    if (lookFree(bot.robotDir)) moveBot(RobotConstants.MOVEMENT.FORWARD)
                }
                else -> {
                    logger.debug{ "Turning around! "}
                    // Turn around
                    moveBot(RobotConstants.MOVEMENT.RIGHT)
                    moveBot(RobotConstants.MOVEMENT.RIGHT)
                }
            }
        }

        private fun moveBot(m: RobotConstants.MOVEMENT) {
            val movementCommand:String = when(m){
                RobotConstants.MOVEMENT.FORWARD -> CommConstants.FORWARD_COMMAND
                RobotConstants.MOVEMENT.BACKWARD -> CommConstants.BACKWARD_COMMAND
                RobotConstants.MOVEMENT.RIGHT -> CommConstants.RIGHT_COMMAND
                RobotConstants.MOVEMENT.LEFT -> CommConstants.LEFT_COMMAND
            }
            logger.debug{ "Sending command $movementCommand"}
            val payload = Gson().toJson(
                mapOf(
                    CommConstants.COMMAND to movementCommand,
                    "units" to 1,
                )
            )

            bot.move(m)
            runBlocking {
                commSession.send(Frame.Text(payload))
                while(true) {
                    when (val frame = commSession.incoming.receive()) {
                        is Frame.Text -> {
                            val reply = frame.readText()
                            logger.debug { "reply is $reply" }
                            val response: Map<String, String> =
                                Gson().fromJson(reply, object : TypeToken<HashMap<String, String>>() {}.type)
                            val type = response["update"]

                            if (type!= null && type=="sensor_read") {
                                val sensorId = response.getValue("id")
                                val sensorValue = response.getValue("value").toInt()
                                logger.debug {
                                    "Processing $sensorId : $sensorValue"
                                }
                                bot.sensorMap[sensorId]!!.processSensorVal(exploredMap, sensorValue)
                            }
                            else {
                                break;
                            }
                        }
//                    is Frame.Binary -> println(frame.readBytes())
                    }
                }
            }
            debugMap(mazeMap = exploredMap, robot = bot)

        }

        private fun lookFree(dir: RobotConstants.DIRECTION): Boolean {
            return when (dir) {
                RobotConstants.DIRECTION.NORTH -> northFree()
                RobotConstants.DIRECTION.EAST -> eastFree()
                RobotConstants.DIRECTION.SOUTH -> southFree()
                RobotConstants.DIRECTION.WEST -> westFree()
            }
        }

        private fun northFree(): Boolean {
            return isExploredNotObstacle(bot.row + 2, bot.col - 1) &&
                    isExploredAndFree(bot.row + 2, bot.col) &&
                    isExploredNotObstacle(bot.row + 2, bot.col + 1)
        }

        private fun eastFree(): Boolean {
            return isExploredNotObstacle(bot.row - 1, bot.col + 2) &&
                    isExploredAndFree(bot.row, bot.col + 2) &&
                    isExploredNotObstacle(bot.row + 1, bot.col + 2)
        }

        private fun southFree(): Boolean {
            return isExploredNotObstacle(bot.row - 2, bot.col - 1) &&
                    isExploredAndFree(bot.row - 2, bot.col) &&
                    isExploredNotObstacle(bot.row - 2, bot.col + 1)
        }

        private fun westFree(): Boolean {
            return isExploredNotObstacle(bot.row - 1, bot.col - 2) &&
                    isExploredAndFree(bot.row, bot.col - 2) &&
                    isExploredNotObstacle(bot.row + 1, bot.col - 2)
        }


        /**
         * Returns true for cells that are explored and not obstacles.
         */
        private fun isExploredNotObstacle(r: Int, c: Int): Boolean {
            if (exploredMap.checkValidCoordinates(r, c)) {
                //logger.debug{ "Checking cell ($r,$c) = ${exploredMap.grid[r][c].explored}, ${exploredMap.grid[r][c].obstacle}"}
                return (exploredMap.grid[r][c].explored && !(exploredMap.grid[r][c].obstacle))
            }
            return false
        }

        /**
         * Returns true for cells that are explored, not virtual walls and not obstacles.
         */
        private fun isExploredAndFree(r: Int, c: Int): Boolean {
            if (exploredMap.checkValidCoordinates(r, c)) {
//            logger.debug{ "Checking cell ($r,$c) = ${exploredMap.grid[r][c].explored}, ${exploredMap.grid[r][c].obstacle}, ${exploredMap.grid[r][c].virtualWall}"}
                return (exploredMap.grid[r][c].explored && !(exploredMap.grid[r][c].obstacle) && !(exploredMap.grid[r][c].virtualWall))
            }
            return false
        }
    }


}

