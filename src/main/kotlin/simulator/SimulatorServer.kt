package simulator

import com.google.gson.Gson
import constants.CommConstants
import constants.CommConstants.BACKWARD_COMMAND
import constants.CommConstants.CALIBRATE_COMMAND
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
    var persistentRealTimeMap = MazeMap()
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
        Simulator.displayMessage("Starting exploration...")
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
        Simulator.displayMessage("Exploration stopped.")
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
        val commandType: String? = request.command
        val response: String
        when {
            request.status != null -> {
                when (request.status) {
                    MOVING_STATUS -> {
                        val units: Int = request.delta!!.toInt()
                        if (units < 0) {
                            for (unit in 1..-units) {
                                robot.move(RobotConstants.MOVEMENT.BACKWARD)
                            }
                        } else {
                            for (unit in 1..units) {
                                robot.move(RobotConstants.MOVEMENT.FORWARD)
                            }
                        }
                    }
                    ROTATING_STATUS -> {
                        val delta: Int = request.delta!!.toInt()
                        if (delta < 0) {
                            for (unit in 1..((-delta) / 90)) {
                                robot.move(RobotConstants.MOVEMENT.LEFT)
                            }
                        } else {
                            for (unit in 1..(delta / 90)) {
                                robot.move(RobotConstants.MOVEMENT.RIGHT)
                            }
                        }
                    }
                    COMPLETED_STATUS -> {
                    }
                }
                response = ""
            }
            request.mapDetect1 != null -> {
                val logMsg = "MDF String received! String is ${request.mapDetect1}, ${request.mapDetect2}"
                logger.info { logMsg }
                Simulator.displayMessage(logMsg)
                response = ""
            }
            request.obstacleDetect != null -> {
                val (xPos, yPos) = request.obstacleDetect
                logger.info { "Received obstacle info at ($xPos, $yPos) " }
                if (realTimeMap.checkValidCoordinates(yPos, xPos)) {
                    realTimeMap.setObstacle(yPos, xPos, true)
                    persistentRealTimeMap.setObstacle(yPos, xPos, true)
                } else {
                    logger.warn { "received coordinate is invalid!" }
                }
                response = ""
//                Simulator.sim.map = realTimeMap
                debugMap(realTimeMap, robot)
            }
            request.exploredDetect != null -> {
                for (pos in request.exploredDetect) {
                    val (xPos, yPos) = pos
                    logger.info { "Received explored info at ($xPos, $yPos) " }
                    if (realTimeMap.checkValidCoordinates(yPos, xPos)) {
                        // Set as explored, remove obstacle (considered as false detect)
                        realTimeMap.setObstacle(yPos, xPos, false)
                        realTimeMap.grid[yPos][xPos].explored = true
                        // Set only explored here, for logging and debug purposes
                        persistentRealTimeMap.grid[yPos][xPos].explored = true
                    } else {
                        logger.warn { "received coordinate is invalid!" }
                    }
                }
                response = ""
//                Simulator.sim.map = realTimeMap
                debugMap(realTimeMap, robot)
            }
            request.imageDetect != null -> {
                val (id: Int, xPos: Int, yPos: Int) = request.imageDetect
                val logMsg = "Image $id detected at ($xPos, $yPos)!"
                logger.info { logMsg }
                Simulator.displayMessage(logMsg)
                response = ""
            }
            request.updateRequest != null -> {
                if (request.updateRequest == SENSOR_READ_COMMAND) {
                    logger.info { "Sensor ${request.id} detected value of ${request.value}" }
                }
                response = ""
            }
            commandType == null -> {
                response = Gson().toJson(UNKNOWN_COMMAND_ERROR)
            }
            commandType.startsWith(CALIBRATE_COMMAND) -> {
                sendSensorTelemetry(sender)
                response = Gson().toJson(COMPLETED_STATUS_MAP)
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
        if (response != "") {
            members[sender]?.send(Frame.Text(response))
        }
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
        loadMapFromDisk(trueMap, DEFAULT_MAP)
        trueMap.setAllExplored()
        updateSimulationUI()
    }

    fun resetExploredMapAndRobot() {
        resetRealTimeMap()
        exploredMap = MazeMap()
        robot.resetRobot()
        robot.simulateSensors(exploredMap, trueMap)
        logger.info{"Robot and map reset invoked successfully!"}
    }

    fun resetRealTimeMap() {
        realTimeMap = MazeMap()
        persistentRealTimeMap = MazeMap()
    }

}