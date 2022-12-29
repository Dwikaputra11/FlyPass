package cthree.admin.flypass.viewmodels

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cthree.admin.flypass.api.APIService
import cthree.admin.flypass.models.postticket.GetPostTicketResponse
import cthree.admin.flypass.models.ticketflight.GetTicketResponse
import cthree.admin.flypass.models.postticket.TicketDataClass
import cthree.admin.flypass.models.putticket.GetPutTicketResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class TicketViewModel@Inject constructor(private val apiService: APIService, application: Application) : ViewModel() {

    private val liveDataTicket: MutableLiveData<GetTicketResponse?> = MutableLiveData()
    private val postDataTicket: MutableLiveData<GetPostTicketResponse?> = MutableLiveData()
    private val updateDataTicket: MutableLiveData<GetPutTicketResponse?> = MutableLiveData()
    private val deleteTicket: MutableLiveData<GetTicketResponse?> = MutableLiveData()

    fun getLiveDataTicket() : MutableLiveData<GetTicketResponse?> {
        return liveDataTicket
    }

    fun getPostDataTicket() : MutableLiveData<GetPostTicketResponse?> {
        return postDataTicket
    }

    fun getPutDataTicket() : MutableLiveData<GetPutTicketResponse?> {
        return updateDataTicket
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

    fun postApiTicket(token: String, ticketDataClass: TicketDataClass){
        apiService.addTickets(token, ticketDataClass)
            .enqueue(object : Callback<GetPostTicketResponse> {
                override fun onResponse(
                    call: Call<GetPostTicketResponse>,
                    response: Response<GetPostTicketResponse>
                ) {
                    if (response.isSuccessful){
                        postDataTicket.postValue(response.body())
                    } else{
                        postDataTicket.postValue(null)
                    }
                }

                override fun onFailure(call: Call<GetPostTicketResponse>, t: Throwable) {
                    postDataTicket.postValue(null)
                }
            })
    }

    fun putApiTicket(token: String, id: Int, ticketDataClass: TicketDataClass){
        apiService.updateTicket(token, id, ticketDataClass)
            .enqueue(object : Callback<GetPutTicketResponse> {
                override fun onResponse(
                    call: Call<GetPutTicketResponse>,
                    response: Response<GetPutTicketResponse>
                ) {
                    if (response.isSuccessful){
                        updateDataTicket.postValue(response.body())
                    } else{
                        updateDataTicket.postValue(null)
                    }
                }

                override fun onFailure(call: Call<GetPutTicketResponse>, t: Throwable) {
                    updateDataTicket.postValue(null)
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