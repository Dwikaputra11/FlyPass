package cthree.admin.flypass.models.airplane


import com.google.gson.annotations.SerializedName

data class GetAirplaneResponse(
    @SerializedName("airplane")
    val airplane: List<Airplane>
)