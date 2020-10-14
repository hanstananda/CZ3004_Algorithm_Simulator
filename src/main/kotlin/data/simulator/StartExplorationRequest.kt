package data.simulator

import com.google.gson.annotations.SerializedName
import constants.CommConstants
import constants.CommConstants.EXPLORATION_START_COMMAND

data class StartExplorationRequest(
    @SerializedName(CommConstants.COVERAGE_LIMIT)
    val coverageLimit: Int?=100){
    val request: String = EXPLORATION_START_COMMAND
}