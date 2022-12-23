package cthree.user.flypass.models.booking.transaction


import com.google.gson.annotations.SerializedName

data class TransactionResponse(
    @SerializedName("bookingId")
    val bookingId: String,
    @SerializedName("message")
    val message: String
)