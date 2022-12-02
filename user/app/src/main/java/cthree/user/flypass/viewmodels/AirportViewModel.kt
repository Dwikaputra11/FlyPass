package cthree.user.flypass.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import cthree.user.flypass.api.APIClient
import cthree.user.flypass.models.airport.Airport
import cthree.user.flypass.models.airport.AirportList
import cthree.user.flypass.models.flight.Flight
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import cthree.user.flypass.repositories.AirportRepository
import cthree.user.flypass.utils.Constants
import cthree.user.flypass.worker.AirportWorker
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class AirportViewModel @Inject constructor(
    private val airportRepository: AirportRepository,
    private val workManager: WorkManager
): ViewModel() {


    private val airportWorkInfo: LiveData<List<WorkInfo>> = workManager.getWorkInfosByTagLiveData(Constants.AIRPORT_WORKER)
    private val liveDataAirport : MutableLiveData<AirportList?> = MutableLiveData()

    fun getAirportWorkerInfo(): LiveData<List<WorkInfo>> = airportWorkInfo

    fun getLiveDataAirports() : MutableLiveData<AirportList?>{
        return liveDataAirport
    }

    fun fetchAirportData(){
        val constraint = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val oneTimeWorkRequest = OneTimeWorkRequest.Builder(AirportWorker::class.java)
            .addTag(Constants.AIRPORT_WORKER)
            .setConstraints(constraint)
            .setInitialDelay(3L, TimeUnit.SECONDS)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            ).build()
        workManager.enqueueUniqueWork(
            Constants.AIRPORT_WORKER,
            ExistingWorkPolicy.KEEP,
            oneTimeWorkRequest,
        )

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

    fun searchAirport(query: String): LiveData<List<Airport>> = airportRepository.searchAirport(query)

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