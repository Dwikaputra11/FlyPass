package cthree.user.flypass.models.flight


import com.google.gson.annotations.SerializedName

data class FlightType(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String
)