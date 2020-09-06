package constants

internal object CommConstants {

    const val COMMAND = "command" // Total number of columns
    const val FORWARD_COMMAND = "move_front"
    const val BACKWARD_COMMAND = "move_back"
    const val RIGHT_COMMAND = "rotate_right"
    const val LEFT_COMMAND = "rotate_left"
    const val MOVEMENT_COMMAND = "move"
    const val ROTATE_COMMAND = "rotate"
    const val IMAGE_COMMAND = "image_detect"
    const val OBSTACLE_DETECT_COMMAND = "obstacle_detect"

    const val SENSOR_READ_COMMAND = "sensor_read"

    val MOVING_STATUS = mapOf("status" to "moving")
    val ROTATING_STATUS = mapOf("status" to "rotating")
    val STOP_STATUS = mapOf("status" to "stopped")
    val SUCCESSFUL_EXECUTION = mapOf("Result" to "Successful")
    val UNKNOWN_COMMAND_ERROR = mapOf("Result" to "Unknown Command!")
    val UNSUPPORTED_COMMAND_ERROR = mapOf("Result" to "Unsupported Command!")
    val EXPLORATION_START_COMMAND = mapOf("request" to "start_explore")
    val FASTEST_PATH_START_COMMAND = mapOf("request" to "start_fastest_path")
}