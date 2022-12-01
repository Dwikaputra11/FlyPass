package cthree.user.flypass.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cthree.user.flypass.api.APIClient
import cthree.user.flypass.models.airport.Airport
import cthree.user.flypass.models.flight.Flight
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AirportViewModel : ViewModel() {

    lateinit var liveDataAirport : MutableLiveData<List<Airport>?>

    init {
        liveDataAirport = MutableLiveData()
    }

    fun getLiveDataAirports() : MutableLiveData<List<Airport>?>{
        return liveDataAirport
    }

    fun callApiAirport(){
        APIClient.instance.apiServiceAirport()
            .enqueue(object : Callback<List<Airport>> {
                override fun onResponse(
                    call: Call<List<Airport>>,
                    response: Response<List<Airport>>
                ) {
                    if (response.isSuccessful){
                        liveDataAirport.postValue(response.body())
                    } else{
                        liveDataAirport.postValue(null)
                    }
                }

                override fun onFailure(call: Call<List<Airport>>, t: Throwable) {
                    liveDataAirport.postValue(null)
                }
            })
    }

}