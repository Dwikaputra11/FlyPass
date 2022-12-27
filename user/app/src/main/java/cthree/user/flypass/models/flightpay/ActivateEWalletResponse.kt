package cthree.user.flypass.models.flightpay


import com.google.gson.annotations.SerializedName

data class ActivateEWalletResponse(
    @SerializedName("message")
    val message: String
)