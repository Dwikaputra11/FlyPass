package cthree.user.flypass.models.booking.searchbook


import com.google.gson.annotations.SerializedName

data class BookingStatus(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String
)