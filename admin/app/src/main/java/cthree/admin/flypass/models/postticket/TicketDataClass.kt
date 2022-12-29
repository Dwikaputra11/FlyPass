package cthree.admin.flypass.models.postticket

data class TicketDataClass(
    val flightCode : String,
    val airlineId : Int,
    val airplaneId : Int,
    val departureAirportId : Int,
    val arrivalAirportId : Int,
    val departureDate : String,
    val departureTime : String,
    val arrivalDate : String,
    val arrivalTime : String,
    val price : Int,
    val flightClassId : Int,
    val baggage : Int,
    val isAvailable : Boolean
)
