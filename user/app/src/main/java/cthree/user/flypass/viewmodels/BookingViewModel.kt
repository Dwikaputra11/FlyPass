package cthree.user.flypass.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cthree.user.flypass.api.ApiService
import cthree.user.flypass.models.booking.response.BookingResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class BookingViewModel @Inject constructor(private val apiService: ApiService): ViewModel(){

    private val bookingResp: MutableLiveData<BookingResponse> = MutableLiveData()

    fun getBookingResp(): LiveData<BookingResponse> = bookingResp

    fun postBookingRequest(bookingResponse: BookingResponse){
        apiService.postBooking(bookingResponse).enqueue(object : Callback<BookingResponse>{
            override fun onResponse(
                call: Call<BookingResponse>,
                response: Response<BookingResponse>
            ) {

            }

            override fun onFailure(call: Call<BookingResponse>, t: Throwable) {

            }

        })
    }
}