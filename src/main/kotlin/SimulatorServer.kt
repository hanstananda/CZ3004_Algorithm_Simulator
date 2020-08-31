import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.channels.ClosedSendChannelException
import mu.KotlinLogging
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

class SimulatorServer {
    private val logger = KotlinLogging.logger {}
    val members = ConcurrentHashMap<String, MutableList<WebSocketSession>>()

    suspend fun help(sender: String) {
        logger.debug{ "Sending help message " }
        members[sender]?.send(Frame.Text("[server::help] Possible commands are: /user, /help and /who"))
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
}