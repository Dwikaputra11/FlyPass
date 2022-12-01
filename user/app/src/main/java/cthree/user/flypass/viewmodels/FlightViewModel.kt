package cthree.user.flypass.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cthree.user.flypass.api.APIClient
import cthree.user.flypass.models.flight.Flight
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FlightViewModel : ViewModel() {

    lateinit var liveDataFlight : MutableLiveData<List<Flight>?>
    lateinit var searchFlight : MutableLiveData<List<Flight>>

    init {
        liveDataFlight = MutableLiveData()
        searchFlight = MutableLiveData()
    }

    fun getLiveDataFlights() : MutableLiveData<List<Flight>?>{
        return liveDataFlight
    }

    fun getSearchFlights() : MutableLiveData<List<Flight>>{
        return searchFlight
    }

    fun callApiFlight(){
        APIClient.instance.apiServiceFlight()
            .enqueue(object : Callback<List<Flight>> {
                override fun onResponse(
                    call: Call<List<Flight>>,
                    response: Response<List<Flight>>
                ) {
                    if (response.isSuccessful){
                        liveDataFlight.postValue(response.body())
                    } else{
                        liveDataFlight.postValue(null)
                    }
                }

                override fun onFailure(call: Call<List<Flight>>, t: Throwable) {
                    liveDataFlight.postValue(null)
                }
            })
    }

    fun callSeacrhFlight(depDate: String, depAirport: String, arrAirport: String){
        APIClient.instance.flightSearch(depDate, depAirport, arrAirport)
            .enqueue(object : Callback<List<Flight>> {
                override fun onResponse(
                    call: Call<List<Flight>>,
                    response: Response<List<Flight>>
                ) {
                    if (response.isSuccessful){
                        liveDataFlight.postValue(response.body())
                    } else{
                        liveDataFlight.postValue(null)
                    }
                }

                override fun onFailure(call: Call<List<Flight>>, t: Throwable) {
                    liveDataFlight.postValue(null)
                }
            })
    }
}