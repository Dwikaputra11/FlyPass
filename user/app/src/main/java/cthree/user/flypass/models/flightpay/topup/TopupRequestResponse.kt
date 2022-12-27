package cthree.user.flypass.models.flightpay.topup


import com.google.gson.annotations.SerializedName

data class TopupRequestResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("request")
    val request: Request
)