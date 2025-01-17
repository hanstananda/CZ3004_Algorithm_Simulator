package data.simulator

import com.google.gson.annotations.SerializedName
import constants.CommConstants.COMMAND
import constants.CommConstants.DELTA_REQUEST
import constants.CommConstants.EXPLORED_DETECT_COMMAND
import constants.CommConstants.IMAGE_COMMAND
import constants.CommConstants.MAP_DETECT_1
import constants.CommConstants.MAP_DETECT_2
import constants.CommConstants.OBSTACLE_DETECT_COMMAND
import constants.CommConstants.STATUS_REQUEST
import constants.CommConstants.UPDATE_REQUEST

data class ParsedRequest(

    @SerializedName(STATUS_REQUEST)
    val status: String?,

    @SerializedName(DELTA_REQUEST)
    val delta: String?,

    @SerializedName(COMMAND)
    val command: String?,

    @SerializedName(OBSTACLE_DETECT_COMMAND)
    val obstacleDetect: Array<Int>?,

    @SerializedName(EXPLORED_DETECT_COMMAND)
    val exploredDetect: Array<Array<Int>>?,

    @SerializedName(MAP_DETECT_1)
    val mapDetect1: String?,

    @SerializedName(MAP_DETECT_2)
    val mapDetect2: String?,

    @SerializedName(IMAGE_COMMAND)
    val imageDetect: Array<Int>?,

    @SerializedName("units")
    val unit: String?="1",

    @SerializedName("angle")
    val angle: String?="90",

    @SerializedName("filename")
    val filename: String?="TestMap1",

    @SerializedName(UPDATE_REQUEST)
    val updateRequest: String?,

    @SerializedName("value")
    val value: Int?,

    @SerializedName("id")
    val id: String?,

    @SerializedName("confidence")
    val confidence: Double,

    ) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ParsedRequest

        if (command != other.command) return false
        if (obstacleDetect != null) {
            if (other.obstacleDetect == null) return false
            if (!obstacleDetect.contentEquals(other.obstacleDetect)) return false
        } else if (other.obstacleDetect != null) return false
        if (imageDetect != null) {
            if (other.imageDetect == null) return false
            if (!imageDetect.contentEquals(other.imageDetect)) return false
        } else if (other.imageDetect != null) return false
        if (exploredDetect != null) {
            if (other.exploredDetect == null) return false
            if (!exploredDetect.contentEquals(other.exploredDetect)) return false
        } else if (other.exploredDetect != null) return false
        if (unit != other.unit) return false
        if (angle != other.angle) return false
        if (filename != other.filename) return false

        return true
    }

    override fun hashCode(): Int {
        var result = command?.hashCode() ?: 0
        result = 31 * result + (obstacleDetect?.contentHashCode() ?: 0)
        result = 31 * result + (imageDetect?.contentHashCode() ?: 0)
        result = 31 * result + (exploredDetect?.contentHashCode() ?: 0)
        result = 31 * result + (unit?.hashCode() ?: 0)
        result = 31 * result + (angle?.hashCode() ?: 0)
        result = 31 * result + (filename?.hashCode() ?: 0)
        return result
    }
}