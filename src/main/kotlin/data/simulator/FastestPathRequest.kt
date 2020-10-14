package data.simulator

import com.google.gson.annotations.SerializedName
import constants.CommConstants

data class FastestPathRequest(
    @SerializedName(CommConstants.WAYPOINT_COMMAND)
    val waypoint: Array<Int>
) {
    @SerializedName(CommConstants.REQUEST_COMMAND)
    val request: String = CommConstants.FASTEST_PATH_START_COMMAND

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FastestPathRequest

        if (request != other.request) return false
        if (!waypoint.contentEquals(other.waypoint)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = request.hashCode()
        result = 31 * result + waypoint.contentHashCode()
        return result
    }

}