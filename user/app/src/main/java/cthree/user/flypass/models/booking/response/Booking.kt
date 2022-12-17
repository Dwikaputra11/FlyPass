package cthree.user.flypass.models.booking.response


import com.google.gson.annotations.SerializedName

data class Booking(
    @SerializedName("bookingCode")
    val bookingCode: String,
    @SerializedName("bookingStatusId")
    val bookingStatusId: Int,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("flight1Id")
    val flight1Id: Int,
    @SerializedName("flight2Id")
    val flight2Id: Int,
    @SerializedName("id")
    val id: Int,
    @SerializedName("passengerContactId")
    val passengerContactId: Int,
    @SerializedName("passengerQty")
    val passengerQty: Int,
    @SerializedName("roundtrip")
    val roundtrip: Boolean,
    @SerializedName("totalPassengerBaggagePrice")
    val totalPassengerBaggagePrice: Int,
    @SerializedName("totalPrice")
    val totalPrice: Int,
    @SerializedName("updatedAt")
    val updatedAt: String,
    @SerializedName("userId")
    val userId: Int
)