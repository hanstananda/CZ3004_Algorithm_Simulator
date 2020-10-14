package data.simulator

import com.google.gson.annotations.SerializedName
import constants.CommConstants
import constants.CommConstants.EXPLORATION_STOP_COMMAND

data class StopExplorationRequest(
    @SerializedName(CommConstants.REASON)
    val reason: String?
) {
    @SerializedName(CommConstants.REQUEST_COMMAND)
    val request: String = EXPLORATION_STOP_COMMAND
}