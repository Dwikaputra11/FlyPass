package cthree.admin.flypass.models.putticket


import com.google.gson.annotations.SerializedName

data class GetPutTicketResponse(
    @SerializedName("flights")
    val flights: Flights,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
)