package cthree.admin.flypass.viewmodels

import android.app.Application
import androidx.lifecycle.*
import androidx.work.*
import cthree.admin.flypass.models.airline.Airline
import cthree.admin.flypass.models.airline.GetAirlineResponse
import cthree.admin.flypass.preferences.UserPreferenceRepository
import cthree.admin.flypass.repositories.AirlineRepository
import cthree.admin.flypass.utils.Constants
import cthree.admin.flypass.worker.AirlineWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class AirlineViewModel @Inject constructor(
    private val airlineRepository: AirlineRepository,
    private val workManager: WorkManager,
    application: Application
): ViewModel() {

    private val prefRepo = UserPreferenceRepository(application.applicationContext)
    val dataUser = prefRepo.readData.asLiveData()

    private val airlineWorkInfo: LiveData<List<WorkInfo>> = workManager.getWorkInfosByTagLiveData(
        Constants.AIRLINE_WORKER)
    private val liveDataAirline : MutableLiveData<GetAirlineResponse?> = MutableLiveData()

    fun getAirlineWorkerInfo(): LiveData<List<WorkInfo>> = airlineWorkInfo

    fun getLiveDataAirline() : MutableLiveData<GetAirlineResponse?> {
        return liveDataAirline
    }

    fun fetchAirlineData(){
        val constraint = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val oneTimeWorkRequest = OneTimeWorkRequest.Builder(AirlineWorker::class.java)
            .addTag(Constants.AIRLINE_WORKER)
            .setConstraints(constraint)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            ).build()
        workManager.enqueueUniqueWork(
            Constants.AIRLINE_WORKER,
            ExistingWorkPolicy.KEEP,
            oneTimeWorkRequest,
        )
    }

    fun existingSearchAirline(query: String): LiveData<List<Airline>> = airlineRepository.existingSearchAirline(query)
    fun searchAirline(query: String): LiveData<List<Airline>> = airlineRepository.searchAirline(query)

    fun getAllAirlineFromDB(): LiveData<List<Airline>> {
        return airlineRepository.getAllAirline()
    }

    fun insertAirlineFromDB(airline: List<Airline>) {
        viewModelScope.launch{
            airlineRepository.insertAirline(airline)
        }
    }

    fun deleteAllAirlineFromDB(){
        viewModelScope.launch {
            airlineRepository.deleteAllAirline()
        }
    }

    fun deleteSingleAirlineFromDB(airline: Airline){
        viewModelScope.launch {
            airlineRepository.deleteSingleAirline(airline)
        }
    }

    fun updateAirlineFromDB(airline: Airline){
        viewModelScope.launch {
            airlineRepository.updateAirline(airline)
        }
    }

    fun addAirlinePrefs(airline: Airline){
        viewModelScope.launch { prefRepo.saveDataAirline(airline) }
    }

//    fun clearUserPref(){
//        viewModelScope.launch { prefRepo.clearDataDepartArrive() }
//    }

}