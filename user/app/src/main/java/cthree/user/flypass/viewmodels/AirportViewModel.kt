package cthree.user.flypass.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cthree.user.flypass.api.APIClient
import cthree.user.flypass.models.airport.Airport
import cthree.user.flypass.models.airport.AirportList
import cthree.user.flypass.models.flight.Flight
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import cthree.user.flypass.repositories.AirportRepository
import kotlinx.coroutines.launch
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

    fun getAllAirportFromDB(): LiveData<List<Airport>> {
        return airportRepository.getAllAirport()
    }

    fun insertAirportFromDB(airport: List<Airport>) {
        viewModelScope.launch{
            airportRepository.insertAirport(airport)
        }
    }

    fun deleteAllAirportFromDB(){
        viewModelScope.launch {
            airportRepository.deleteAllAirport()
        }
    }

    fun deleteSingleAirportFromDB(airport: Airport){
        viewModelScope.launch {
            airportRepository.deleteSingleAirport(airport)
        }
    }

    fun updateAirportFromDB(airport: Airport){
        viewModelScope.launch {
            airportRepository.updateAirport(airport)
        }
    }
}