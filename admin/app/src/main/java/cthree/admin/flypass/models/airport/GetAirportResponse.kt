package cthree.admin.flypass.models.airport


import com.google.gson.annotations.SerializedName

data class GetAirportResponse(
    @SerializedName("airport")
    val airport: List<Airport>
)