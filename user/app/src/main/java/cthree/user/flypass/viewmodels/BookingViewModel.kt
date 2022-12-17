package cthree.user.flypass.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cthree.user.flypass.api.ApiService
import cthree.user.flypass.models.booking.bookings.BookingListResponse
import cthree.user.flypass.models.booking.request.BookingRequest
import cthree.user.flypass.models.booking.response.BookingResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

private const val TAG = "BookingViewModel"

@HiltViewModel
class BookingViewModel @Inject constructor(private val apiService: ApiService): ViewModel(){

    val bookingResp: MutableLiveData<BookingResponse?>                  = MutableLiveData()
    private val searchBooking: MutableLiveData<BookingListResponse?>    = MutableLiveData()
    private val errorSearchBookMsg: MutableLiveData<String?>            = MutableLiveData()
    private val userBooking: MutableLiveData<BookingListResponse?>      = MutableLiveData()

    fun getBookingResp(): LiveData<BookingResponse?> = bookingResp
    fun getSearchBooking(): LiveData<BookingListResponse?> = searchBooking
    fun userBookingResponse(): LiveData<BookingListResponse?> = userBooking

    fun postBookingRequest(token: String?,bookingRequest: BookingRequest){
        val tokens = if(token?.isNotEmpty() == true) "Bearer $token" else null
        apiService.postBooking(tokens,bookingRequest).enqueue(object : Callback<BookingResponse>{
            override fun onResponse(
                call: Call<BookingResponse>,
                response: Response<BookingResponse>
            ) {
                if(response.isSuccessful){
                    bookingResp.postValue(response.body())
                }else{
                    bookingResp.postValue(null)
                }
            }

            override fun onFailure(call: Call<BookingResponse>, t: Throwable) {
                bookingResp.postValue(null)
            }

        })
    }

    fun getUserBooking(token: String){
        apiService.userBookings("Bearer $token").enqueue(object : Callback<BookingListResponse>{
            override fun onResponse(
                call: Call<BookingListResponse>,
                response: Response<BookingListResponse>
            ) {
                if(response.isSuccessful){
                    userBooking.postValue(response.body())
                }else{
                    userBooking.postValue(null)
                }
            }

            override fun onFailure(call: Call<BookingListResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.localizedMessage}")
            }

        })
    }

    fun searchBookingCode(bookingCode: String){
        apiService.searchBookingByCode(bookingCode).enqueue(object : Callback<BookingListResponse>{
            override fun onResponse(call: Call<BookingListResponse>, response: Response<BookingListResponse>) {
                if(response.isSuccessful){
                    searchBooking.postValue(response.body())
                }else{
                    searchBooking.postValue(null)
                }
            }

            override fun onFailure(call: Call<BookingListResponse>, t: Throwable) {
                searchBooking.postValue(null)
            }

        })
    }
}