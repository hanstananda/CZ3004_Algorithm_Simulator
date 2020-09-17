import io.ktor.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import mu.KotlinLogging

// connect to ws://localhost:8080/robot

fun main(args: Array<String>) {
    val logger = KotlinLogging.logger {}
    embeddedServer(Netty, commandLineEnvironment(args)).start()
    logger.info {"Application server successfully started!"}
}
