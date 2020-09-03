package simulator

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import constants.CommConstants.BACKWARD_COMMAND
import constants.CommConstants.COMMAND
import constants.CommConstants.FORWARD_COMMAND
import constants.CommConstants.IMAGE_COMMAND
import constants.CommConstants.LEFT_COMMAND
import constants.CommConstants.MOVEMENT_COMMAND
import constants.CommConstants.RIGHT_COMMAND
import constants.CommConstants.ROTATE_COMMAND
import constants.CommConstants.SUCCESSFUL_EXECUTION
import constants.CommConstants.UNKNOWN_COMMAND_ERROR
import constants.CommConstants.UNSUPPORTED_COMMAND_ERROR
import constants.RobotConstants
import constants.RobotConstants.START_COL
import constants.RobotConstants.START_ROW
import data.map.MazeMap
import data.robot.Robot
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.channels.ClosedSendChannelException
import mu.KotlinLogging
import utils.map.loadMapFromDisk
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList


class SimulatorServer {
    private val logger = KotlinLogging.logger {}
    private val members = ConcurrentHashMap<String, MutableList<WebSocketSession>>()
    var mazeMap = MazeMap()
    var exploredMap = MazeMap()
    val robot = Robot(START_ROW, START_COL)

    init {
        loadMapFromDisk(mazeMap, "BlankMap")
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

    suspend fun message(sender: String, message: String) {
        val request: Map<String, String> =
            Gson().fromJson(message, object : TypeToken<HashMap<String, String>>() {}.type)

        // Pre-format the message to be send, to prevent doing it for all the users or connected sockets.
        val commandType: String? = request[COMMAND]
        var response = ""
        when {
            commandType == null -> {
                response = Gson().toJson(UNKNOWN_COMMAND_ERROR)
            }
            commandType.startsWith(MOVEMENT_COMMAND) -> {
                val units = (request["units"] ?: "1").toInt()
                when (commandType) {
                    FORWARD_COMMAND -> {
                        for (unit in 1..units) {
                            robot.move(RobotConstants.MOVEMENT.FORWARD)
                        }
                        response = Gson().toJson(SUCCESSFUL_EXECUTION)
                        sendSensorTelemetry(sender)
                    }
                    BACKWARD_COMMAND -> {
                        for (unit in 1..units) {
                            robot.move(RobotConstants.MOVEMENT.BACKWARD)
                        }
                        response = Gson().toJson(SUCCESSFUL_EXECUTION)
                        sendSensorTelemetry(sender)
                    }
                    else -> {
                        response = Gson().toJson(UNKNOWN_COMMAND_ERROR)
                    }
                }
            }
            commandType.startsWith(ROTATE_COMMAND) -> {
                val angle = (request["angle"] ?: "90").toInt()
                when (commandType) {
                    RIGHT_COMMAND -> {
                        for (unit in 1..(angle / 90)) {
                            robot.move(RobotConstants.MOVEMENT.RIGHT)
                        }
                        response = Gson().toJson(SUCCESSFUL_EXECUTION)
                        sendSensorTelemetry(sender)
                    }
                    LEFT_COMMAND -> {
                        for (unit in 1..(angle / 90)) {
                            robot.move(RobotConstants.MOVEMENT.LEFT)
                        }
                        response = Gson().toJson(SUCCESSFUL_EXECUTION)
                        sendSensorTelemetry(sender)
                    }
                    else -> {
                        response = Gson().toJson(UNKNOWN_COMMAND_ERROR)
                    }
                }
            }
            commandType.startsWith(IMAGE_COMMAND) -> {
                response = Gson().toJson(UNSUPPORTED_COMMAND_ERROR)
            }
            else -> {
                response = Gson().toJson(UNKNOWN_COMMAND_ERROR)
            }
        }
        members[sender]?.send(Frame.Text(response))
    }


    private suspend fun sendSensorTelemetry(sender: String) {
        val sensorReadings = robot.getSensorReadings(exploredMap, mazeMap)
        for (result in sensorReadings) {
            val response = Gson().toJson(
                mapOf(
                    "update" to "sensor_read",
                    "id" to result.key,
                    "value" to result.value
                )
            )
            members[sender]?.send(Frame.Text(response))
        }
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
}