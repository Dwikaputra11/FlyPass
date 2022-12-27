package cthree.user.flypass.models.flightpay.topup


import com.google.gson.annotations.SerializedName

data class Request(
    @SerializedName("balance")
    val balance: Int,
    @SerializedName("bookingId")
    val bookingId: Any,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("status")
    val status: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("updatedAt")
    val updatedAt: String,
    @SerializedName("walletId")
    val walletId: Int
)