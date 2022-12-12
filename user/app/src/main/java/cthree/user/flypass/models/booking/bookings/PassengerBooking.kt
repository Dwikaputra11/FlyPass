package cthree.user.flypass.models.booking.bookings


import com.google.gson.annotations.SerializedName

data class PassengerBooking(
    @SerializedName("bookingId")
    val bookingId: Int,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("passengerId")
    val passengerId: Int,
    @SerializedName("updatedAt")
    val updatedAt: String
)