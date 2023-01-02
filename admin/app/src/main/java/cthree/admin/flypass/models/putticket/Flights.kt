package cthree.admin.flypass.models.putticket


import com.google.gson.annotations.SerializedName

data class Flights(
    @SerializedName("airlineId")
    val airlineId: String,
    @SerializedName("airplaneId")
    val airplaneId: String,
    @SerializedName("arrivalAirportId")
    val arrivalAirportId: String,
    @SerializedName("arrivalDate")
    val arrivalDate: String,
    @SerializedName("arrivalTime")
    val arrivalTime: String,
    @SerializedName("baggage")
    val baggage: String,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("departureAirportId")
    val departureAirportId: String,
    @SerializedName("departureDate")
    val departureDate: String,
    @SerializedName("departureTime")
    val departureTime: String,
    @SerializedName("duration")
    val duration: String,
    @SerializedName("flightClassId")
    val flightClassId: String,
    @SerializedName("flightCode")
    val flightCode: String,
    @SerializedName("flightTypeId")
    val flightTypeId: Int,
    @SerializedName("id")
    val id: Int,
    @SerializedName("isAvailable")
    val isAvailable: Boolean,
    @SerializedName("price")
    val price: String,
    @SerializedName("updatedAt")
    val updatedAt: String
)