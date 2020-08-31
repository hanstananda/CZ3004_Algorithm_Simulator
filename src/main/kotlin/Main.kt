import io.ktor.server.engine.*
import io.ktor.server.netty.*

// connect to ws://localhost:8080/ws

fun main(args: Array<String>) {
    embeddedServer(Netty, commandLineEnvironment(args)).start()
}
