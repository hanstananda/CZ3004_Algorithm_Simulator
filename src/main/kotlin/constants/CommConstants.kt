package constants

import com.google.gson.annotations.SerializedName
import java.io.Serializable

object CommConstants {

    const val STATUS_REQUEST = "status"
    const val REQUEST_COMMAND = "request"
    const val MOVING_STATUS = "moving"
    const val ROTATING_STATUS = "rotating"
    const val COMPLETED_STATUS = "completed"
    const val DELTA_REQUEST = "delta"
    const val COMMAND = "command" // Total number of columns
    const val FORWARD_COMMAND = "move_front"
    const val BACKWARD_COMMAND = "move_backward"
    const val RIGHT_COMMAND = "rotate_right"
    const val LEFT_COMMAND = "rotate_left"
    const val MOVEMENT_COMMAND = "move"
    const val ROTATE_COMMAND = "rotate"
    const val IMAGE_COMMAND = "image_detect"
    const val TAKE_IMAGE_COMMAND = "take_image"
    const val OBSTACLE_DETECT_COMMAND = "obstacle_detect"
    const val EXPLORED_DETECT_COMMAND = "explored_detect"
    const val LOAD_TEST_MAP_COMMAND = "load_test_map"
    const val CALIBRATE_COMMAND = "calibrate"
    const val MAP_DETECT_1 = "map_detect_1"
    const val MAP_DETECT_2 = "map_detect_2"
    const val WAYPOINT_COMMAND = "waypoint"
    const val COVERAGE_LIMIT = "coverageLimit"
    const val REASON = "reason"


    const val SENSOR_READ_COMMAND = "sensor_read"

    val MOVING_STATUS_MAP = mapOf("status" to MOVING_STATUS)
    val ROTATING_STATUS_MAP = mapOf("status" to ROTATING_STATUS)
    val COMPLETED_STATUS_MAP = mapOf("status" to COMPLETED_STATUS)
    val FINISHED_COMMAND = mapOf("status" to "Command executed!")
    val UNKNOWN_COMMAND_ERROR = mapOf("status" to "Unknown Command!")
    val UNSUPPORTED_COMMAND_ERROR = mapOf("status" to "Unsupported Command!")
    const val EXPLORATION_START_COMMAND = "start_explore"
    const val EXPLORATION_STOP_COMMAND  = "stop_explore"
    const val FASTEST_PATH_START_COMMAND = "start_fastest_path"

    const val UPDATE_REQUEST = "update"
    const val IMAGE_ID = "image_id"

}