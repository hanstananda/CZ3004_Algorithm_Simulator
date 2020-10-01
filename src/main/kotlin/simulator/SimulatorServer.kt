package simulator

import com.google.gson.Gson
import constants.CommConstants
import constants.CommConstants.BACKWARD_COMMAND
import constants.CommConstants.COMPLETED_STATUS
import constants.CommConstants.FASTEST_PATH_START_COMMAND
import constants.CommConstants.FINISHED_COMMAND
import constants.CommConstants.FORWARD_COMMAND
import constants.CommConstants.LEFT_COMMAND
import constants.CommConstants.LOAD_TEST_MAP_COMMAND
import constants.CommConstants.MOVEMENT_COMMAND
import constants.CommConstants.RIGHT_COMMAND
import constants.CommConstants.ROTATE_COMMAND
import constants.CommConstants.SENSOR_READ_COMMAND
import constants.CommConstants.COMPLETED_STATUS_MAP
import constants.CommConstants.EXPLORATION_STOP_COMMAND
import constants.CommConstants.MOVING_STATUS
import constants.CommConstants.ROTATING_STATUS
import constants.CommConstants.UNKNOWN_COMMAND_ERROR
import constants.MapConstants.DEFAULT_MAP
import constants.RobotConstants
import constants.RobotConstants.START_COL
import constants.RobotConstants.START_ROW
import data.map.MazeMap
import data.robot.Robot
import data.simulator.ParsedRequest
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ClosedSendChannelException
import mu.KotlinLogging
import utils.map.RandomMapGenerator
import utils.map.debugMap
import utils.map.loadMapFromDisk
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.TimeUnit


object SimulatorServer {
    private val logger = KotlinLogging.logger {}
    private val members = ConcurrentHashMap<String, MutableList<WebSocketSession>>()
    lateinit var latestMember: String
    var trueMap = MazeMap()
    var exploredMap = MazeMap()
    var realTimeMap = MazeMap()
    val robot = Robot(START_ROW, START_COL)

    init {
        Simulator.sim = SimulatorMap(trueMap, robot)
        resetToInitialServerState()
        Simulator.displayFrame()
    }

    private fun updateSimulationUI() {
//        if (logger.isDebugEnabled) {
//            debugMap(mazeMap = exploredMap, robot = robot)
//        }
        Simulator.updateSimulatorMap()
    }

    suspend fun help(sender: String) {
        logger.debug { "Sending help message " }
        members[sender]?.send(Frame.Text("[server::help] Possible commands are: /help and /hello"))
    }

    suspend fun memberJoin(member: String, socket: WebSocketSession) {
        // Checks if this user is already registered in the server and gives him/her a temporal name if required.
//        val name = memberNames.computeIfAbsent(member) { "user${usersCounter.incrementAndGet()}" }

        // Associates this socket to the member id.
        // Since iteration is likely to happen more frequently than adding new items,
        // we use a `CopyOnWriteArrayList`.
        // We could also control how many sockets we would allow per client here before appending it.
        // But since this is a sample we are not doing it.
        val list = members.computeIfAbsent(member) { CopyOnWriteArrayList<WebSocketSession>() }
        list.add(socket)
        latestMember = member

//        // Only when joining the first socket for a member notifies the rest of the users.
//        if (list.size == 1) {
//            broadcast("server", "Member joined: $name.")
//        }

        // Sends the user the latest messages from this server to let the member have a bit context.
//        val messages = synchronized(lastMessages) { lastMessages.toList() }
//        for (message in messages) {
//            socket.send(Frame.Text(message))
//        }
    }

    /**
     * Handles that a [member] with a specific [socket] left the server.
     */
    suspend fun memberLeft(member: String, socket: WebSocketSession) {
        // Removes the socket connection for this member
        val connections = members[member]
        connections?.remove(socket)

        // If no more sockets are connected for this member, let's remove it from the server
        // and notify the rest of the users about this event.
        if (connections != null && connections.isEmpty()) {
//            val name = memberNames.remove(member) ?: member
//            broadcast("server", "Member left: $name.")
        }
    }

    fun handleStartExploration(timeout: Int = -1, coverageLimit: Int = 100) {
        when {
            timeout != -1 -> {
                GlobalScope.launch(Dispatchers.Default) {
                    logger.info { "Starting exploration with timeout of $timeout" }
                    startExplorationWithCoverage(coverageLimit)
                    delay(TimeUnit.SECONDS.toMillis(timeout.toLong()))
                    stopExploration("Timeout reached")
                }
            }
            else -> {
                GlobalScope.launch {
                    startExplorationWithCoverage(coverageLimit)
                }
            }
        }
    }

    suspend fun startExplorationWithCoverage(coverageLimit: Int = 100) {
        val startExplorationCommandObject = CommConstants.StartExplorationCommand(coverageLimit)
        val command = Gson().toJson(startExplorationCommandObject)
        members[latestMember]?.send(Frame.Text(command))
    }

    suspend fun stopExploration(reason: String = "force-stop") {
        val commandMap = HashMap(EXPLORATION_STOP_COMMAND) // copy the basic command
        commandMap["reason"] = reason
        val command = Gson().toJson(commandMap)
        members[latestMember]?.send(Frame.Text(command))
        logger.info { "Sent stop exploration with reason $reason" }
    }

    suspend fun startFastestPathWithWaypoint(x: Int = START_COL, y: Int = START_ROW) {
        val commandMap = HashMap(FASTEST_PATH_START_COMMAND) // copy the basic command
        commandMap["waypoint"] = "[$x,$y]"
        val command = Gson().toJson(commandMap)
        members[latestMember]?.send(Frame.Text(command))
    }

    fun generateRandomMap() {
        trueMap = RandomMapGenerator.createValidatedRandomMazeMap()
        updateSimulationUI()
    }

    suspend fun message(sender: String, message: String) {
        val request =
            Gson().fromJson(message, ParsedRequest::class.java)

        // Pre-format the message to be send, to prevent doing it for all the users or connected sockets.
        val status: String? = request.status
        val commandType: String? = request.command
        val obstacleDetect: Array<Int>? = request.obstacleDetect
        val imageDetect: Array<Int>? = request.imageDetect
        val exploredDetect: Array<Array<Int>>? = request.exploredDetect
        val response: String
        when {
            status != null -> {
                when(status) {
                    MOVING_STATUS -> {
                        val units:Int = request.delta!!.toInt()
                        for (unit in 1..units) {
                            robot.move(RobotConstants.MOVEMENT.FORWARD)
                        }
                    }
                    ROTATING_STATUS -> {
                        val delta:Int = request.delta!!.toInt()
                        if(delta<0) {
                            for (unit in 1..( (-delta) / 90)) {
                                robot.move(RobotConstants.MOVEMENT.LEFT)
                            }
                        }
                        else {
                            for (unit in 1..(delta / 90)) {
                                robot.move(RobotConstants.MOVEMENT.RIGHT)
                            }
                        }
                    }
                    COMPLETED_STATUS -> {
                    }
                }
                response = Gson().toJson(COMPLETED_STATUS_MAP)
            }
            obstacleDetect != null -> {
                val (xPos, yPos) = obstacleDetect
                logger.info { "Received obstacle info at ($xPos, $yPos) " }
                if (realTimeMap.checkValidCoordinates(yPos, xPos)) {
                    realTimeMap.setObstacle(yPos, xPos, true)
                } else {
                    logger.warn { "received coordinate is invalid!" }
                }
                response = Gson().toJson(FINISHED_COMMAND)
//                Simulator.sim.map = realTimeMap
//                debugMap(realTimeMap, robot)
            }
            exploredDetect != null -> {
                for(pos in exploredDetect) {
                    val (xPos, yPos) = pos
                    logger.info { "Received explored info at ($xPos, $yPos) " }
                    if (realTimeMap.checkValidCoordinates(yPos, xPos)) {
                        realTimeMap.grid[yPos][xPos].explored = true
                    } else {
                        logger.warn { "received coordinate is invalid!" }
                    }
                }
                response = Gson().toJson(FINISHED_COMMAND)
//                Simulator.sim.map = realTimeMap
//                debugMap(realTimeMap, robot)
            }
            imageDetect != null -> {
                logger.info { imageDetect }
                response = Gson().toJson(FINISHED_COMMAND)
            }
            commandType == null -> {
                response = Gson().toJson(UNKNOWN_COMMAND_ERROR)
            }
            commandType.startsWith(MOVEMENT_COMMAND) -> {
                val units = (request.unit ?: "1").toInt()
                when (commandType) {
                    FORWARD_COMMAND -> {
                        for (unit in 1..units) {
                            robot.move(RobotConstants.MOVEMENT.FORWARD)
                        }
                        response = Gson().toJson(COMPLETED_STATUS_MAP)
                        sendSensorTelemetry(sender)
                    }
                    BACKWARD_COMMAND -> {
                        for (unit in 1..units) {
                            robot.move(RobotConstants.MOVEMENT.BACKWARD)
                        }
                        response = Gson().toJson(COMPLETED_STATUS_MAP)
                        sendSensorTelemetry(sender)
                    }
                    else -> {
                        response = Gson().toJson(UNKNOWN_COMMAND_ERROR)
                    }
                }
            }
            commandType.startsWith(ROTATE_COMMAND) -> {
                val angle = (request.angle ?: "90").toInt()
                when (commandType) {
                    RIGHT_COMMAND -> {
                        for (unit in 1..(angle / 90)) {
                            robot.move(RobotConstants.MOVEMENT.RIGHT)
                        }
                        response = Gson().toJson(COMPLETED_STATUS_MAP)
                        sendSensorTelemetry(sender)
                    }
                    LEFT_COMMAND -> {
                        for (unit in 1..(angle / 90)) {
                            robot.move(RobotConstants.MOVEMENT.LEFT)
                        }
                        response = Gson().toJson(COMPLETED_STATUS_MAP)
                        sendSensorTelemetry(sender)
                    }
                    else -> {
                        response = Gson().toJson(UNKNOWN_COMMAND_ERROR)
                    }
                }
            }
            commandType.startsWith(LOAD_TEST_MAP_COMMAND) -> {
                val filename = request.filename ?: "TestMap1"
                loadMapFromDisk(trueMap, filename)
                response = Gson().toJson(FINISHED_COMMAND)
            }
            else -> {
                response = Gson().toJson(UNKNOWN_COMMAND_ERROR)
            }
        }
        members[sender]?.send(Frame.Text(response))
        updateSimulationUI()
    }


    private suspend fun sendSensorTelemetry(sender: String) {
        val sensorReadings = robot.getSensorReadings(exploredMap, trueMap)
        for (result in sensorReadings) {
            val response = Gson().toJson(
                mapOf(
                    "update" to SENSOR_READ_COMMAND,
                    "id" to result.key,
                    "value" to result.value
                )
            )
            logger.debug { response }
            members[sender]?.send(Frame.Text(response))
        }
        debugMap(exploredMap)
    }

    /**
     * Sends a [message] to a list of [this] [WebSocketSession].
     */
    suspend fun List<WebSocketSession>.send(frame: Frame) {
        forEach {
            try {
                it.send(frame.copy())
            } catch (t: Throwable) {
                try {
                    it.close(CloseReason(CloseReason.Codes.PROTOCOL_ERROR, ""))
                } catch (ignore: ClosedSendChannelException) {
                    // at some point it will get closed
                }
            }
        }
    }

    suspend fun sendTo(recipient: String, sender: String, message: String) {
        members[recipient]?.send(Frame.Text("[$sender] $message"))
    }

    fun resetToInitialServerState() {
        resetExploredMapAndRobot()
        resetRealTimeMap()
        loadMapFromDisk(trueMap, DEFAULT_MAP)
        trueMap.setAllExplored()
        updateSimulationUI()
    }

    fun resetExploredMapAndRobot() {
        exploredMap = MazeMap()
        robot.resetRobot()
        robot.simulateSensors(exploredMap, trueMap)
    }

    fun resetRealTimeMap() {
        realTimeMap = MazeMap()
    }

}