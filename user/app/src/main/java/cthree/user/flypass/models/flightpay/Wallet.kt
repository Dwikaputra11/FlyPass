package cthree.user.flypass.models.flightpay


import com.google.gson.annotations.SerializedName

data class Wallet(
    @SerializedName("balance")
    val balance: Int,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("updatedAt")
    val updatedAt: String
)