package cthree.admin.flypass.models.ticketflight


import com.google.gson.annotations.SerializedName

data class GetTicketResponse(
    @SerializedName("flights")
    val flights: List<Flight>
)