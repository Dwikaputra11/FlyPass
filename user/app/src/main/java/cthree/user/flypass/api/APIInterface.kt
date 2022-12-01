package cthree.user.flypass.api

import cthree.user.flypass.models.airport.Airport
import cthree.user.flypass.models.flight.Flight
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface APIInterface {

    @GET("v1/flights")
    fun apiServiceFlight() : Call<List<Flight>>

    @GET("v1/flights/search?")
    fun flightSearch(@Query("depDate") depDate: String,
                     @Query("depAirport") depAirport: String,
                     @Query("arrAirport") arrAirport: String) : Call<List<Flight>>

    @GET("v1/airport")
    fun apiServiceAirport() : Call<List<Airport>>

}