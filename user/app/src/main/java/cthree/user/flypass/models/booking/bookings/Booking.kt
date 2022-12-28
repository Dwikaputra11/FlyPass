package cthree.user.flypass.models.booking.bookings


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Parcelize
data class Booking(
    @SerializedName("bookingCode")
    val bookingCode: String,
    @SerializedName("BookingStatus")
    val bookingStatus: BookingStatus,
    @SerializedName("bookingStatusId")
    val bookingStatusId: Int,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("flight1")
    val depFlight: cthree.user.flypass.models.flight.Flight,
    @SerializedName("flight1Id")
    val flight1Id: Int,
    @SerializedName("flight2")
    val arrFlight: cthree.user.flypass.models.flight.Flight,
    @SerializedName("flight2Id")
    val flight2Id: Int,
    @SerializedName("id")
    val id: Int,
    @SerializedName("PassengerContact")
    val passengerContact: PassengerContact,
    @SerializedName("passengerContactId")
    val passengerContactId: Int,
    @SerializedName("passengerQty")
    val passengerQty: Int,
    @SerializedName("Passengers")
    val passengers: List<Passenger>,
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
): Parcelable