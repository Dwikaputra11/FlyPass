package cthree.admin.flypass.models.update

data class ForUpdate(
    val idTicket : Int,
    val flightNumber : String,
    val departAirportCity : String,
    val arriveAirportCity : String,
    val airlineName : String,
    val airplaneType : String,
    val calendarDepart : String,
    val calendarArrival : String,
    val timeDepart : String,
    val timeArrival : String,
    val price : Int,
    val spSeatClass : Int
)
