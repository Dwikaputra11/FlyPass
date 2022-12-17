package cthree.user.flypass.models.booking.request


import com.google.gson.annotations.SerializedName

data class Passenger(
    @SerializedName("baggage")
    var baggage: MutableList<String>,
    @SerializedName("firstName")
    val firstName: String,
    @SerializedName("identityNumber")
    val identityNumber: String,
    @SerializedName("identityType")
    val identityType: String,
    @SerializedName("lastName")
    val lastName: String
)