package data.simulator

import com.google.gson.annotations.SerializedName
import constants.CommConstants.UPDATE_REQUEST
import constants.CommConstants.IMAGE_COMMAND
import constants.CommConstants.IMAGE_ID


data class ImageDetectResponse(
    @SerializedName(UPDATE_REQUEST)
    val update: String=IMAGE_COMMAND,

    @SerializedName(IMAGE_ID)
    val id: Integer,

) {

}