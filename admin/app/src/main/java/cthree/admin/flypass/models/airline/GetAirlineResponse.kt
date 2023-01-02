package cthree.admin.flypass.models.airline


import com.google.gson.annotations.SerializedName

data class GetAirlineResponse(
    @SerializedName("airlines")
    val airlines: List<Airline>
)