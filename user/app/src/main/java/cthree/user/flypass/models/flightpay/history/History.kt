package cthree.user.flypass.models.flightpay.history


import com.google.gson.annotations.SerializedName

data class History(
    @SerializedName("balance")
    val balance: Int,
    @SerializedName("Booking")
    val booking: Any,
    @SerializedName("id")
    val id: Int,
    @SerializedName("status")
    val status: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("updatedAt")
    val updatedAt: String
)