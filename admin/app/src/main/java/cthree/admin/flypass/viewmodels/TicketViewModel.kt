package cthree.admin.flypass.viewmodels

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cthree.admin.flypass.api.APIService
import cthree.admin.flypass.models.ticketflight.GetTicketResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class TicketViewModel@Inject constructor(private val apiService: APIService, application: Application) : ViewModel() {

    private val liveDataTicket: MutableLiveData<GetTicketResponse?> = MutableLiveData()
    private val deleteTicket: MutableLiveData<GetTicketResponse?> = MutableLiveData()

    fun getLiveDataTicket() : MutableLiveData<GetTicketResponse?> {
        return liveDataTicket
    }

    fun callApiTicket(){
        apiService.getAllTickets()
            .enqueue(object : Callback<GetTicketResponse> {
                override fun onResponse(
                    call: Call<GetTicketResponse>,
                    response: Response<GetTicketResponse>
                ) {
                    if (response.isSuccessful){
                        liveDataTicket.postValue(response.body())
                    } else{
                        liveDataTicket.postValue(null)
                    }
                }

                override fun onFailure(call: Call<GetTicketResponse>, t: Throwable) {
                    liveDataTicket.postValue(null)
                }
            })
    }

    fun callDeleteTicket(token : String, id: Int) {
        apiService.deleteTicket(token, id)
            .enqueue(object : Callback<GetTicketResponse> {
                override fun onResponse(
                    call: Call<GetTicketResponse>,
                    response: Response<GetTicketResponse>
                ) {
                    if (response.isSuccessful){
                        deleteTicket.postValue(response.body())
                    }else{
                        deleteTicket.postValue(null)
                    }
                }

                override fun onFailure(call: Call<GetTicketResponse>, t: Throwable) {
                    deleteTicket.postValue(null)
                }

            })
    }
}