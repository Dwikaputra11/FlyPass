package cthree.user.flypass.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cthree.user.flypass.api.APIClient
import cthree.user.flypass.models.airport.Airport
import cthree.user.flypass.models.airport.AirportList
import cthree.user.flypass.models.flight.Flight
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class AirportViewModel @Inject constructor(): ViewModel() {

    private val liveDataAirport : MutableLiveData<AirportList?> = MutableLiveData()

    fun getLiveDataAirports() : MutableLiveData<AirportList?>{
        return liveDataAirport
    }

    fun callApiAirport(){
        APIClient.instance.apiServiceAirport()
            .enqueue(object : Callback<AirportList?> {
                override fun onResponse(
                    call: Call<AirportList?>,
                    response: Response<AirportList?>
                ) {
                    if (response.isSuccessful){
                        liveDataAirport.postValue(response.body())
                    } else{
                        liveDataAirport.postValue(null)
                    }
                }

                override fun onFailure(call: Call<AirportList?>, t: Throwable) {
                    liveDataAirport.postValue(null)
                }
            })
    }

}