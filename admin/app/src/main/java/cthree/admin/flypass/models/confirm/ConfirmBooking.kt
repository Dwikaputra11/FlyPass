package cthree.admin.flypass.models.confirm


import com.google.gson.annotations.SerializedName

data class ConfirmBooking(
    @SerializedName("message")
    val message: String,
    @SerializedName("transaction")
    val transaction: Transaction
)