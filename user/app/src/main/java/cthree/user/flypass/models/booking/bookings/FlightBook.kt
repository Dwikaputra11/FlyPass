package cthree.user.flypass.models.booking.bookings


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class FlightBook(
    @SerializedName("Airline")
    val airline: Airline,
    @SerializedName("Airplane")
    val airplane: Airplane,
    @SerializedName("arrivalAirport")
    val arrivalAirport: Airport,
    @SerializedName("arrivalDate")
    val arrivalDate: String,
    @SerializedName("arrivalTime")
    val arrivalTime: String,
    @SerializedName("baggage")
    val baggage: Int,
    @SerializedName("departureAirport")
    val departureAirport: Airport,
    @SerializedName("departureDate")
    val departureDate: String,
    @SerializedName("departureTime")
    val departureTime: String,
    @SerializedName("duration")
    val duration: String,
    @SerializedName("FlightClass")
    val flightClass: FlightClass,
    @SerializedName("flightCode")
    val flightCode: String,
    @SerializedName("FlightType")
    val flightType: FlightType,
    @SerializedName("id")
    val id: Int,
    @SerializedName("isAvailable")
    val isAvailable: Boolean,
    @SerializedName("price")
    val price: Int
) : Parcelable