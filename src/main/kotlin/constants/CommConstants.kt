package constants

const val COMMAND = "command" // Total number of columns
const val FORWARD_COMMAND = "move_front"
const val BACKWARD_COMMAND = "move_back"
const val RIGHT_COMMAND = "rotate_right"
const val LEFT_COMMAND = "rotate_left"
const val MOVEMENT_COMMAND = "move"
const val ROTATE_COMMAND = "rotate"
const val IMAGE_COMMAND = "image_detect"

val UNKNOWN_COMMAND_ERROR = mapOf("Error" to "Unknown Command!")
val UNSUPPORTED_COMMAND_ERROR = mapOf("Error" to "Unsupported Command!")