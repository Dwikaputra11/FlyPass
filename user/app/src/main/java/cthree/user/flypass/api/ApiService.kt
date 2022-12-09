package cthree.user.flypass.api

import cthree.user.flypass.models.airport.AirportList
import cthree.user.flypass.models.booking.request.BookingRequest
import cthree.user.flypass.models.booking.response.BookingResponse
import cthree.user.flypass.models.booking.searchbook.Booking
import cthree.user.flypass.models.booking.searchbook.SearchBook
import cthree.user.flypass.models.flight.FlightList
import cthree.user.flypass.models.login.Login
import cthree.user.flypass.models.login.LoginData
import cthree.user.flypass.models.user.RegisterResponse
import cthree.user.flypass.models.user.RegisterUser
import cthree.user.flypass.models.user.User
import cthree.user.flypass.models.wishlist.delete.DeleteWishlist
import cthree.user.flypass.models.wishlist.get.WishList
import cthree.user.flypass.models.wishlist.post.WishlistResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
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
    fun registerUser(@Body request : RegisterUser) : Call<RegisterResponse>

    @POST("v1/login")
    fun loginUser(@Body login: LoginData): Call<Login>

    @POST("v1/flights/books")
    fun postBooking(@Header("Authorization") token: String?,@Body booking: BookingRequest): Call<BookingResponse>

    @GET("v1/bookings/search")
    fun searchBookingByCode(@Query("bookingcode") bookingCode: String): Call<SearchBook>

    @POST("v1/whistlist/{idFlight}")
    fun addWishlist(@Header("Authorization") token: String, @Path("idFlight") id: Int): Call<WishlistResponse>

    @GET("v1/whistlist")
    fun getUserWishlist(@Header("Authorization") token: String): Call<WishList>

    @DELETE("v1/whistlist/{id}")
    fun deleteWishlist(@Header("Authorization") token: String, @Path("id") id: Int): Call<DeleteWishlist>
}