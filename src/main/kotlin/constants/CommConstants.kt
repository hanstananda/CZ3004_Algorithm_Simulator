package constants

import com.google.gson.annotations.SerializedName
import java.io.Serializable

object CommConstants {

    const val STATUS_REQUEST = "status"
    const val MOVING_STATUS = "moving"
    const val ROTATING_STATUS = "rotating"
    const val COMPLETED_STATUS = "completed"
    const val DELTA_REQUEST = "delta"
    const val COMMAND = "command" // Total number of columns
    const val FORWARD_COMMAND = "move_front"
    const val BACKWARD_COMMAND = "move_back"
    const val RIGHT_COMMAND = "rotate_right"
    const val LEFT_COMMAND = "rotate_left"
    const val MOVEMENT_COMMAND = "move"
    const val ROTATE_COMMAND = "rotate"
    const val IMAGE_COMMAND = "image_detect"
    const val OBSTACLE_DETECT_COMMAND = "obstacle_detect"
    const val EXPLORED_DETECT_COMMAND = "explored_detect"
    const val LOAD_TEST_MAP_COMMAND = "load_test_map"

    const val SENSOR_READ_COMMAND = "sensor_read"

    val MOVING_STATUS_MAP = mapOf("status" to MOVING_STATUS)
    val ROTATING_STATUS_MAP = mapOf("status" to ROTATING_STATUS)
    val COMPLETED_STATUS_MAP = mapOf("status" to COMPLETED_STATUS)
    val FINISHED_COMMAND = mapOf("status" to "Command executed!")
    val UNKNOWN_COMMAND_ERROR = mapOf("status" to "Unknown Command!")
    val UNSUPPORTED_COMMAND_ERROR = mapOf("status" to "Unsupported Command!")
    data class StartExplorationCommand(val coverageLimit: Int = 100){
        val request: String = "start_explore"
    }
    val EXPLORATION_STOP_COMMAND  = mapOf("request" to "stop_explore")
    val FASTEST_PATH_START_COMMAND = mapOf("request" to "start_fastest_path")
}