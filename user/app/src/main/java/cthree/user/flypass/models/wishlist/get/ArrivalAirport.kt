package cthree.user.flypass.models.wishlist.get


import com.google.gson.annotations.SerializedName

data class ArrivalAirport(
    @SerializedName("city")
    val city: String,
    @SerializedName("country")
    val country: String,
    @SerializedName("iata")
    val iata: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String
)