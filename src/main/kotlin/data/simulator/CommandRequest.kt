package data.simulator

import com.google.gson.annotations.SerializedName
import constants.CommConstants.REQUEST_COMMAND

data class CommandRequest(
    @SerializedName(REQUEST_COMMAND)
    val request: String,
) {

}