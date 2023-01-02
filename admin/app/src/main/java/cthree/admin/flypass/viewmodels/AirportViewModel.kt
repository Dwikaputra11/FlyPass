package cthree.admin.flypass.viewmodels

import android.app.Application
import androidx.lifecycle.*
import androidx.work.*
import cthree.admin.flypass.models.airport.Airport
import cthree.admin.flypass.models.airport.GetAirportResponse
import cthree.admin.flypass.preferences.UserPreferenceRepository
import cthree.admin.flypass.repositories.AirportRepository
import cthree.admin.flypass.utils.Constants
import cthree.admin.flypass.worker.AirportWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class AirportViewModel @Inject constructor(
    private val airportRepository: AirportRepository,
    private val workManager: WorkManager,
    application: Application
) : ViewModel() {

    private val prefRepo = UserPreferenceRepository(application.applicationContext)
    val dataUser = prefRepo.readData.asLiveData()

    private val airportWorkInfo: LiveData<List<WorkInfo>> = workManager.getWorkInfosByTagLiveData(
        Constants.AIRPORT_WORKER)
    private val liveDataAirport : MutableLiveData<GetAirportResponse?> = MutableLiveData()

    fun getAirportWorkerInfo(): LiveData<List<WorkInfo>> = airportWorkInfo

    fun getLiveDataAirports() : MutableLiveData<GetAirportResponse?> {
        return liveDataAirport
    }

    fun fetchAirportData(){
        val constraint = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val oneTimeWorkRequest = OneTimeWorkRequest.Builder(AirportWorker::class.java)
            .addTag(Constants.AIRPORT_WORKER)
            .setConstraints(constraint)
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
    }

    fun existingSearchAirport(query: String): LiveData<List<Airport>> = airportRepository.existingSearchAirport(query)
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

    fun addAirportDepartPrefs(airport: Airport){
        viewModelScope.launch { prefRepo.saveDataAirportDepart(airport) }
    }

    fun addAirportArrivePrefs(airport: Airport){
        viewModelScope.launch { prefRepo.saveDataAirportArrive(airport) }
    }

//    fun clearUserPref(){
//        viewModelScope.launch { prefRepo.clearDataDepartArrive() }
//    }

}