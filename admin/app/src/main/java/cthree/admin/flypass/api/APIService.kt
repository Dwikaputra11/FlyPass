package cthree.admin.flypass.api

import cthree.admin.flypass.models.admin.AdminDataClass
import cthree.admin.flypass.models.admin.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface APIService {

    @POST("v1/login")
    fun loginAdmin(@Body request : AdminDataClass) : Call<User>

//    @GET("v1/whoami")
//    fun loginAdmin(@Header("Authorization") token: String) : Call<>
}