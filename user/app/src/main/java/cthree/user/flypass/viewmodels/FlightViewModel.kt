package cthree.user.flypass.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cthree.user.flypass.api.ApiService
import cthree.user.flypass.models.flight.FlightList
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class FlightViewModel @Inject constructor(private val apiService: ApiService) : ViewModel() {

    private val liveDataFlight : MutableLiveData<FlightList?> = MutableLiveData()
    private val searchFlight : MutableLiveData<FlightList?> = MutableLiveData()

    fun getLiveDataFlights() : MutableLiveData<FlightList?>{
        return liveDataFlight
    }

    fun getSearchFlights() : MutableLiveData<FlightList?>{
        return searchFlight
    }

    fun callApiFlight(){
        apiService.apiServiceFlight()
            .enqueue(object : Callback<FlightList> {
                override fun onResponse(
                    call: Call<FlightList>,
                    response: Response<FlightList>
                ) {
                    if (response.isSuccessful){
                        liveDataFlight.postValue(response.body())
                    } else{
                        liveDataFlight.postValue(null)
                    }
                }

                override fun onFailure(call: Call<FlightList>, t: Throwable) {
                    liveDataFlight.postValue(null)
                }
            })
    }

    fun callSearchFlight(depDate: String, depAirport: String, arrAirport: String){
        apiService.flightSearch(depDate, depAirport, arrAirport)
            .enqueue(object : Callback<FlightList> {
                override fun onResponse(
                    call: Call<FlightList>,
                    response: Response<FlightList>
                ) {
                    if (response.isSuccessful){
                        searchFlight.postValue(response.body())
                    } else{
                        searchFlight.postValue(null)
                    }
                }

                override fun onFailure(call: Call<FlightList>, t: Throwable) {
                    searchFlight.postValue(null)
                }
            })
    }
}