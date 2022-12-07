package cthree.user.flypass.models.booking.searchbook


import com.google.gson.annotations.SerializedName

data class Flight1(
    @SerializedName("airlineId")
    val airlineId: Int,
    @SerializedName("airplaneId")
    val airplaneId: Int,
    @SerializedName("arrivalAirportId")
    val arrivalAirportId: Int,
    @SerializedName("arrivalDate")
    val arrivalDate: String,
    @SerializedName("arrivalTime")
    val arrivalTime: String,
    @SerializedName("baggage")
    val baggage: Int,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("departureAirportId")
    val departureAirportId: Int,
    @SerializedName("departureDate")
    val departureDate: String,
    @SerializedName("departureTime")
    val departureTime: String,
    @SerializedName("duration")
    val duration: String,
    @SerializedName("flightClassId")
    val flightClassId: Int,
    @SerializedName("flightCode")
    val flightCode: String,
    @SerializedName("flightTypeId")
    val flightTypeId: Int,
    @SerializedName("id")
    val id: Int,
    @SerializedName("isAvailable")
    val isAvailable: Boolean,
    @SerializedName("price")
    val price: Int,
    @SerializedName("updatedAt")
    val updatedAt: String
)