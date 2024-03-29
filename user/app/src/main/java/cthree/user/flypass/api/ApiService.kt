package cthree.user.flypass.api

import cthree.user.flypass.data.*
import cthree.user.flypass.models.airport.AirportList
import cthree.user.flypass.models.booking.bookings.BookingListResponse
import cthree.user.flypass.models.booking.request.BookingRequest
import cthree.user.flypass.models.booking.response.BookingResponse
import cthree.user.flypass.models.booking.transaction.TransactionResponse
import cthree.user.flypass.models.flight.FlightList
import cthree.user.flypass.models.flightpay.ActivateEWalletResponse
import cthree.user.flypass.models.flightpay.BookingBalancePay
import cthree.user.flypass.models.flightpay.UserWallet
import cthree.user.flypass.models.flightpay.history.HistoryTopupList
import cthree.user.flypass.models.flightpay.topup.TopupConfirmation
import cthree.user.flypass.models.flightpay.topup.TopupRequestResponse
import cthree.user.flypass.models.login.Login
import cthree.user.flypass.models.login.LoginData
import cthree.user.flypass.models.login.refreshtoken.RefreshToken
import cthree.user.flypass.models.notification.NotificationList
import cthree.user.flypass.models.notification.UpdateNotify
import cthree.user.flypass.models.user.*
import cthree.user.flypass.models.wishlist.delete.DeleteWishlist
import cthree.user.flypass.models.wishlist.get.WishList
import cthree.user.flypass.models.wishlist.post.WishlistResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
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
    fun getUserProfile(@Header("Authorization") token: String) : Call<User>

    @POST("v1/register")
    fun registerUser(@Body request : RegisterUser) : Call<RegisterResponse>

    @POST("v1/googleregister")
    fun registerGoogle(@Body request: RegisterGoogle): Call<RegisterResponse>

    @POST("v1/login")
    fun loginUser(@Body login: LoginData): Call<Login>

    @POST("v1/googlelogin")
    fun googleIdTokenLogin(@Body request: GoogleTokenRequest): Call<Login>

    @GET("v1/refresh")
    fun refreshToken(@Header("Authorization") token: String): Call<RefreshToken>

    @GET("v1/logout")
    fun logout(@Header("Authorization") token: String): Call<String>

    @PUT("v1/user")
    @Multipart
    fun updatePhotoProfile(
        @Header("Authorization") token: String,
        @Part("name") name: RequestBody,
        @Part("email") email: RequestBody,
        @Part("phone") phone: RequestBody?,
        @Part image: MultipartBody.Part,
        @Part("gender") gender: RequestBody?,
        @Part("birthDate") birthDate: RequestBody?
    ): Call<UpdateProfileResponse>

    @PUT("v1/user")
    fun updateProfile(@Header("Authorization")token: String, @Body profile: UpdateProfile): Call<UpdateProfileResponse>

    @POST("v1/flights/books")
    fun postBooking(@Header("Authorization") token: String?,@Body booking: BookingRequest): Call<BookingResponse>

    @Multipart
    @POST("v1/pay/create/{bookingId}")
    fun postPaymentBooking(
        @Header("Authorization") token: String?,
        @Path("bookingId") bookingId: Int,
        @Part image: MultipartBody.Part,
    ): Call<TransactionResponse>

    @GET("v1/bookings/search")
    fun searchBookingByCode(@Query("bookingcode") bookingCode: String): Call<BookingListResponse>

    @GET("v1/bookings")
    fun userBookings(@Header("Authorization") token :String): Call<BookingListResponse>

    @POST("v1/wishlist/{idFlight}")
    fun addWishlist(@Header("Authorization") token: String, @Path("idFlight") id: Int): Call<WishlistResponse>

    @GET("v1/wishlist")
    fun getUserWishlist(@Header("Authorization") token: String): Call<WishList>

    @DELETE("v1/wishlist/{id}")
    fun deleteWishlist(@Header("Authorization") token: String, @Path("id") id: Int): Call<DeleteWishlist>

    //notification
    @GET("v1/notification")
    fun getNotification(@Header("Authorization") token: String): Call<NotificationList>

    @PUT("v1/notification/{id}")
    fun updateNotification(@Header("Authorization") token: String, @Path("id") id: Int): Call<UpdateNotify>


    // Wallet
    @POST("v1/wallet")
    fun activateEWallet(@Body pin: InputPinMember, @Header("Authorization") token: String): Call<ActivateEWalletResponse>

    @GET("v1/wallet")
    fun userWallet(@Header("Authorization") token: String): Call<UserWallet>

    @POST("v1/wallet/payment/{bookingId}")
    fun bookingWithBalance(
        @Path("bookingId") id: Int,
        @Header("Authorization") token: String,
        @Body pin: InputPinMember
    ): Call<BookingBalancePay>

    @POST("v1/wallet/topup")
    fun topUpRequest(
        @Header("Authorization") token: String,
        @Body amount: BalanceRequestAmount,
    ): Call<TopupRequestResponse>

    @POST("v1/wallet/topup/confirm/{walletHistoryId}")
    fun topUpConfirm(
        @Header("Authorization") token: String,
        @Path("walletHistoryId") id: Int,
    ): Call<TopupConfirmation>

    @GET("v1/wallet/history")
    fun walletHistory(@Header("Authorization") token: String): Call<HistoryTopupList>

}