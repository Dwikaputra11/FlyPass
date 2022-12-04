package cthree.user.flypass.models.booking


import com.google.gson.annotations.SerializedName

data class Booking(
    @SerializedName("bookingCode")
    val bookingCode: String,
    @SerializedName("bookingStatusId")
    val bookingStatusId: Int,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("flight1")
    val flightBooking: FlightBooking,
    @SerializedName("flight1Id")
    val flight1Id: Int,
    @SerializedName("flight2")
    val flight2: FlightBooking,
    @SerializedName("flight2Id")
    val flight2Id: Int,
    @SerializedName("id")
    val id: Int,
    @SerializedName("passengerQty")
    val passengerQty: Int,
    @SerializedName("Passengers")
    val passengers: List<Passenger>,
    @SerializedName("roundtrip")
    val roundtrip: Boolean,
    @SerializedName("totalPrice")
    val totalPrice: Int,
    @SerializedName("updatedAt")
    val updatedAt: String,
    @SerializedName("userId")
    val userId: Any
)