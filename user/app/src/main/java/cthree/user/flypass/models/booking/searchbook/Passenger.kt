package cthree.user.flypass.models.booking.searchbook


import com.google.gson.annotations.SerializedName

data class Passenger(
    @SerializedName("age")
    val age: Any,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("firstName")
    val firstName: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("identityNumber")
    val identityNumber: String,
    @SerializedName("identityType")
    val identityType: String,
    @SerializedName("lastName")
    val lastName: String,
    @SerializedName("PassengerBooking")
    val passengerBooking: PassengerBooking,
    @SerializedName("updatedAt")
    val updatedAt: String
)