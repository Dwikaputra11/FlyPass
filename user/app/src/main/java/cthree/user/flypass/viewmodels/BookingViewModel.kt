package cthree.user.flypass.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cthree.user.flypass.api.ApiService
import cthree.user.flypass.models.booking.request.BookingRequest
import cthree.user.flypass.models.booking.response.BookingResponse
import cthree.user.flypass.models.booking.searchbook.Booking
import cthree.user.flypass.models.booking.searchbook.SearchBook
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class BookingViewModel @Inject constructor(private val apiService: ApiService): ViewModel(){

    private val bookingResp: MutableLiveData<BookingResponse?>  = MutableLiveData()
    private val searchBooking: MutableLiveData<SearchBook?>     = MutableLiveData()
    private val errorSearchBookMsg: MutableLiveData<String?>    = MutableLiveData()

    fun getBookingResp(): LiveData<BookingResponse?> = bookingResp
    fun getSearchBooking(): LiveData<SearchBook?> = searchBooking

    fun postBookingRequest(token: String?,bookingRequest: BookingRequest){
        apiService.postBooking(token,bookingRequest).enqueue(object : Callback<BookingResponse>{
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

    fun searchBookingCode(bookingCode: String){
        apiService.searchBookingByCode(bookingCode).enqueue(object : Callback<SearchBook>{
            override fun onResponse(call: Call<SearchBook>, response: Response<SearchBook>) {
                if(response.isSuccessful){
                    searchBooking.postValue(response.body())
                }else{
                    searchBooking.postValue(null)
                }
            }

            override fun onFailure(call: Call<SearchBook>, t: Throwable) {
                searchBooking.postValue(null)
            }

        })
    }
}