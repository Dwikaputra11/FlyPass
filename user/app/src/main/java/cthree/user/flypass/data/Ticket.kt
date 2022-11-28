package cthree.user.flypass.data

data class Ticket (
    val departureTime: String,
    val arrivalTime: String,
    val aitaDeparture: String,
    val aitaArrival: String,
    val duration: Int,
    val airplaneName: String,
    val price: Int,
    val seatClass: String,
    val flightCode: String
)