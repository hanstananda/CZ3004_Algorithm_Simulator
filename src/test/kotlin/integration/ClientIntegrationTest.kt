package integration

import io.ktor.client.*
import io.ktor.client.features.websocket.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ClientIntegrationTest {
    private val logger = KotlinLogging.logger {}

    private val client = HttpClient {
        install(WebSockets)
    }

    @Test
    fun conversationTest() {
        runBlocking {
            client.ws(
                method = HttpMethod.Get,
                host = "127.0.0.1",
                port = 8080, path = "/robot"
            ) { // this: DefaultClientWebSocketSession

                // Send text frame.
                send("/hello")

//                // Send text frame.
//                send(Frame.Text("Hello World"))
//
//                // Send binary frame.
//                send(Frame.Binary(...))

                // Receive frame.
                when (val frame = incoming.receive()) {
                    is Frame.Text -> {
                        val reply = frame.readText()
                        logger.info { reply }
                        assertTrue(reply.contains("Hello"))
                    }
//                    is Frame.Binary -> println(frame.readBytes())
                }
                client.close()
            }
        }
    }
}