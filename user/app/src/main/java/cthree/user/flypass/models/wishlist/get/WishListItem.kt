package cthree.user.flypass.models.wishlist.get


import com.google.gson.annotations.SerializedName

<<<<<<<< HEAD:user/app/src/main/java/cthree/user/flypass/models/wishlist/get/WishListItem.kt
data class WishListItem(
    @SerializedName("Airline")
    val airline: Airline,
    @SerializedName("Airplane")
    val airplane: Airplane,
    @SerializedName("arrivalAirport")
    val arrivalAirport: ArrivalAirport,
========
data class FlightBook(
    @SerializedName("airlineId")
    val airlineId: Int,
    @SerializedName("airplaneId")
    val airplaneId: Int,
    @SerializedName("arrivalAirportId")
    val arrivalAirportId: Int,
>>>>>>>> 8997074858f12554b1f331233198aae919a0cc89:user/app/src/main/java/cthree/user/flypass/models/wishlist/get/FlightBook.kt
    @SerializedName("arrivalDate")
    val arrivalDate: String,
    @SerializedName("arrivalTime")
    val arrivalTime: String,
    @SerializedName("baggage")
    val baggage: Int,
    @SerializedName("departureAirport")
    val departureAirport: DepartureAirport,
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
)