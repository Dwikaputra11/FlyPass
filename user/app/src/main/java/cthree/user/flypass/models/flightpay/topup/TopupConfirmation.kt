package cthree.user.flypass.models.flightpay.topup


import com.google.gson.annotations.SerializedName

data class TopupConfirmation(
    @SerializedName("message")
    val message: String
)