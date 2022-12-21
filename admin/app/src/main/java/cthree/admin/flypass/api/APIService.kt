package cthree.admin.flypass.api

import cthree.admin.flypass.models.admin.AdminDataClass
import cthree.admin.flypass.models.admin.LoginAdminResponse
import cthree.admin.flypass.models.admin.RegisterAdminDataClass
import cthree.admin.flypass.models.admin.RegisterAdminResponse
import cthree.admin.flypass.models.ticketflight.Flight
import cthree.admin.flypass.models.ticketflight.GetTicketResponse
import cthree.admin.flypass.models.user.GetUserResponse
import cthree.admin.flypass.models.user.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
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
}