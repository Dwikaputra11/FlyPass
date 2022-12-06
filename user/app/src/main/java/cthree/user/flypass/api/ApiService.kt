package cthree.user.flypass.api

import cthree.user.flypass.models.airport.AirportList
import cthree.user.flypass.models.flight.FlightList
import cthree.user.flypass.models.user.UpdateProfile
import cthree.user.flypass.models.user.RegisterResponse
import cthree.user.flypass.models.user.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    @GET("v1/flights")
    fun apiServiceFlight() : Call<FlightList>

    @GET("v1/flights/search?")
    fun flightSearch(@Query("depDate") depDate: String,
                     @Query("depAirport") depAirport: String,
                     @Query("arrAirport") arrAirport: String) : Call<FlightList>

    @GET("v1/airport")
    fun apiServiceAirport() : Call<AirportList>

    @GET("v1/user")
    fun apiServiceUser() : Call<User>

    @POST("v1/register")
    fun registerUser(@Body request : UpdateProfile) : Call<RegisterResponse>
}