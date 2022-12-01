package cthree.user.flypass.models.airport


import com.google.gson.annotations.SerializedName

data class Airport(
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