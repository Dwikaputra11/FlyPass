package cthree.user.flypass.models.airport


import com.google.gson.annotations.SerializedName

data class AirportList(
    @SerializedName("airport")
    val airport: List<Airport>
)