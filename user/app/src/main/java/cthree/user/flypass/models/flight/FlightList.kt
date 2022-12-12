package cthree.user.flypass.models.flight


import com.google.gson.annotations.SerializedName

data class FlightList(
    @SerializedName("flights")
    val flights: List<Flight>
)