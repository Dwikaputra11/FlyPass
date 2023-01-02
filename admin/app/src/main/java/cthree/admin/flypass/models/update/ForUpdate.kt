package cthree.admin.flypass.models.update

data class ForUpdate(
    val idTicket : Int,
    val flightNumber : String,
    val departAirportCity : String,
    val departAirportId : Int,
    val arriveAirportCity : String,
    val arriveAirportId : Int,
    val airlineName : String,
    val airlineId : Int,
    val airplaneType : String,
    val airplaneId : Int,
    val calendarDepart : String,
    val calendarArrival : String,
    val timeDepart : String,
    val timeArrival : String,
    val price : Int,
    val spSeatClass : Int
)
