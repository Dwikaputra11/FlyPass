package cthree.user.flypass.utils

object Constants {
    // Service
    const val BASE_URL = "https://flypass-api.up.railway.app/"

    // Navigation Argument
    const val FLIGHT_DIR = "flight_dir"
    const val DEPART_DATE = "depDate"
    const val ARRIVE_DATE = "arrDate"
    const val DEP_AIRPORT = "depAirport"
    const val ARR_AIRPORT = "arrAirport"
    const val ROUND_TRIP = "roundTrip"
    const val DEPART_DATE_TV = "departDateTv"
    const val ARRIVE_DATE_TV = "arriveDateTv"
    const val DEP_FLIGHT = "depFlight"
    const val ARR_FLIGHT = "arrFlight"

    // Shared Preferences
    const val PREF_NAME                 = "shared_pref"
    const val PASSENGER_AMOUNT          = "passenger_amount"
    const val IS_FIRST_INSTALL          = "isFirstInstall"
    const val IS_AIRPORT_DB_EXIST       = "isAirportDBExist"
    const val DEPART_AIRPORT            = "departAirport"
    const val DEPART_AIRPORT_CITY       = "departAirportCity"
    const val DEPART_AIRPORT_COUNTRY    = "departAirportCountry"
    const val DEPART_AIRPORT_IATA       = "departAirportIata"
    const val DEPART_AIRPORT_ID         = "departAirportId"
    const val DEPART_AIRPORT_NAME       = "departAirportName"
    const val ARRIVE_AIRPORT            = "arriveAirport"
    const val ARRIVE_AIRPORT_CITY       = "arriveAirportCity"
    const val ARRIVE_AIRPORT_COUNTRY    = "arriveAirportCountry"
    const val ARRIVE_AIRPORT_IATA       = "arriveAirportIata"
    const val ARRIVE_AIRPORT_ID         = "arriveAirportId"
    const val ARRIVE_AIRPORT_NAME       = "arriveAirportName"
    const val DEPART_DEFAULT_VAL        = "departDefaultVal"
    const val ARRIVE_DEFAULT_VAL        = "arriveDefaultVal"
    const val SEAT_CLASS                = "seatClass"
    const val USER_ID                   = "userId"

    // WORK MANAGER
    const val AIRPORT_WORKER = "airport_worker"

    // TIME CONVERT
    const val TIME_TYPE = "time_type"
    const val DATE_TYPE = "date_type"

    const val USER_TOKEN = "user_token"
}