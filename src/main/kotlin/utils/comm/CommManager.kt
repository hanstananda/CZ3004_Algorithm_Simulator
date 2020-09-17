package utils.comm

import mu.KotlinLogging
import java.io.*
import java.net.Socket
import java.net.UnknownHostException

object CommManager {
    lateinit var conn: Socket
    lateinit var writer: BufferedWriter
    lateinit var reader: BufferedReader
    private val logger = KotlinLogging.logger {}

    fun openConnection() {
        logger.info{ "Opening connection..." }
        try {
            val HOST = "192.168.2.1"
            val PORT = 8008
            conn = Socket(HOST, PORT)
            writer = BufferedWriter(OutputStreamWriter(BufferedOutputStream(conn.getOutputStream())))
            reader = BufferedReader(InputStreamReader(conn.getInputStream()))
            logger.info { "Connection established successfully!" }
            return
        } catch (e: UnknownHostException) {
            logger.error { "UnknownHostException encountered during connection establishment!" }
        } catch (e: IOException) {
            logger.error { "IOException encountered during connection establishment!" }
        } catch (e: Exception) {
            logger.error { "Unknown Exception encountered during connection establishment! $e"  }
        }
        println("Failed to establish connection!")
    }

    fun sendMsg(msg: String?, msgType: String) {
        logger.debug{ "Opening connection..." }
        try {
            val outputMsg: String = if (msg == null) {
                msgType.trimIndent()
            }  else {
                "$msgType $msg".trimIndent()
            }
            logger.debug { "Sending out message: $outputMsg "}
            writer.write(outputMsg)
            writer.flush()
        } catch (e: IOException) {
            logger.error { "IOException encountered during message sending!" }
        } catch (e: java.lang.Exception) {
            logger.error { "Unknown Exception encountered during message sending! $e"  }
            println(e.toString())
        }
    }


}