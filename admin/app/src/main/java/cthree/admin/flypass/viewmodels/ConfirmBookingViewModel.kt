package cthree.admin.flypass.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cthree.admin.flypass.api.APIService
import cthree.admin.flypass.models.confirm.ConfirmBooking
import cthree.admin.flypass.models.transaction.TransactionList
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

private const val TAG = "ConfirmBookingViewModel"

@HiltViewModel
class ConfirmBookingViewModel @Inject constructor(private val apiService: APIService):ViewModel() {

    private val confirmStatus: MutableLiveData<ConfirmBooking?> = MutableLiveData()
    private val allTransaction: MutableLiveData<TransactionList?> = MutableLiveData()
    private val rejectStatus: MutableLiveData<ConfirmBooking?> = MutableLiveData()

    fun getConfirmStatus(): LiveData<ConfirmBooking?> = confirmStatus
    fun getAllTransaction(): LiveData<TransactionList?> = allTransaction
    fun getRejectStatus(): LiveData<ConfirmBooking?> = rejectStatus

    fun allTransaction(token: String){
        apiService.getAllTransaction("Bearer $token").enqueue(object : Callback<TransactionList>{
            override fun onResponse(
                call: Call<TransactionList>,
                response: Response<TransactionList>
            ) {
                if(response.isSuccessful){
                    allTransaction.postValue(response.body())
                }else{
                    allTransaction.postValue(null)
                }
            }

            override fun onFailure(call: Call<TransactionList>, t: Throwable) {
                allTransaction.postValue(null)
                Log.e(TAG, "All Transaction: ${t.localizedMessage}")
            }

        })
    }

    fun confirmBooking(id: Int, token: String){
        apiService.confirmTicket(id, "Bearer $token").enqueue(object : Callback<ConfirmBooking>{
            override fun onResponse(
                call: Call<ConfirmBooking>,
                response: Response<ConfirmBooking>
            ) {
                if(response.isSuccessful){
                    confirmStatus.postValue(response.body())
                }else{
                    confirmStatus.postValue(null)
                }
            }

            override fun onFailure(call: Call<ConfirmBooking>, t: Throwable) {
                confirmStatus.postValue(null)
                Log.e(TAG, "Confirm Ticket: ${t.localizedMessage}")
            }

        })
    }

    fun rejectBooking(id: Int, token: String){
        apiService.rejectTicket(id, "Bearer $token").enqueue(object : Callback<ConfirmBooking>{
            override fun onResponse(
                call: Call<ConfirmBooking>,
                response: Response<ConfirmBooking>
            ) {
                if(response.isSuccessful){
                    rejectStatus.postValue(response.body())
                }else{
                    rejectStatus.postValue(null)
                }
            }

            override fun onFailure(call: Call<ConfirmBooking>, t: Throwable) {
                rejectStatus.postValue(null)
                Log.e(TAG, "Reject Ticket: ${t.localizedMessage}")
            }

        })
    }
}