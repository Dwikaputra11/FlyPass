package cthree.admin.flypass.models.postticket


import com.google.gson.annotations.SerializedName

data class GetPostTicketResponse(
    @SerializedName("flights")
    val flights: Flights,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
)