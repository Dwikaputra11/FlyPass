package cthree.admin.flypass.api

import cthree.admin.flypass.models.admin.AdminDataClass
import cthree.admin.flypass.models.admin.LoginAdminResponse
import cthree.admin.flypass.models.admin.RegisterAdminDataClass
import cthree.admin.flypass.models.admin.RegisterAdminResponse
import cthree.admin.flypass.models.airline.GetAirlineResponse
import cthree.admin.flypass.models.airplane.GetAirplaneResponse
import cthree.admin.flypass.models.airport.GetAirportResponse
import cthree.admin.flypass.models.postticket.GetPostTicketResponse
import cthree.admin.flypass.models.ticketflight.GetTicketResponse
import cthree.admin.flypass.models.postticket.TicketDataClass
import cthree.admin.flypass.models.putticket.GetPutTicketResponse
import cthree.admin.flypass.models.user.GetUserResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface APIService {

    @POST("v1/login")
    fun loginAdmin(@Body request : AdminDataClass) : Call<LoginAdminResponse>

    @GET("v1/getalluser")
    fun getAllUser(@Header("Authorization") token: String) : Call<GetUserResponse>

    @POST("v1/register/admin")
    fun registerAdmin(@Header("Authorization") token: String, @Body request : RegisterAdminDataClass) : Call<RegisterAdminResponse>

    @GET("v1/flights")
    fun getAllTickets() : Call<GetTicketResponse>

    @POST("v1/flights")
    fun addTickets(@Header("Authorization") token: String, @Body request : TicketDataClass) : Call<GetPostTicketResponse>

    @PUT("v1/flights/{id}")
    fun updateTicket(@Header("Authorization") token: String, @Path("id") id : Int, @Body requst : TicketDataClass) : Call<GetPutTicketResponse>

    @DELETE("v1/flights/{id}")
    fun deleteTicket(@Header("Authorization") token: String, @Path("id") id : Int) : Call<GetTicketResponse>

    @GET("v1/airport")
    fun apiServiceAirport() : Call<GetAirportResponse>

    @GET("v1/airlines")
    fun apiServiceAirline() : Call<GetAirlineResponse>

    @GET("v1/airplanes")
    fun apiServiceAirplane() : Call<GetAirplaneResponse>
}