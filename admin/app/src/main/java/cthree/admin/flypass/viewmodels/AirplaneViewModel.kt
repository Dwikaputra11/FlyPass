package cthree.admin.flypass.viewmodels

import android.app.Application
import androidx.lifecycle.*
import androidx.work.*
import cthree.admin.flypass.models.airplane.Airplane
import cthree.admin.flypass.models.airplane.GetAirplaneResponse
import cthree.admin.flypass.preferences.UserPreferenceRepository
import cthree.admin.flypass.repositories.AirplaneRepository
import cthree.admin.flypass.utils.Constants
import cthree.admin.flypass.worker.AirplaneWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class AirplaneViewModel @Inject constructor(
    private val airplaneRepository: AirplaneRepository,
    private val workManager: WorkManager,
    application: Application
) : ViewModel() {

    private val prefRepo = UserPreferenceRepository(application.applicationContext)
    val dataUser = prefRepo.readData.asLiveData()

    private val airplaneWorkInfo: LiveData<List<WorkInfo>> = workManager.getWorkInfosByTagLiveData(
        Constants.AIRPLANE_WORKER)
    private val liveDataAirplane : MutableLiveData<GetAirplaneResponse?> = MutableLiveData()

    fun getAirplaneWorkerInfo(): LiveData<List<WorkInfo>> = airplaneWorkInfo

    fun getLiveDataAirplane() : MutableLiveData<GetAirplaneResponse?> {
        return liveDataAirplane
    }

    fun fetchAirplaneData(){
        val constraint = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val oneTimeWorkRequest = OneTimeWorkRequest.Builder(AirplaneWorker::class.java)
            .addTag(Constants.AIRPLANE_WORKER)
            .setConstraints(constraint)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            ).build()
        workManager.enqueueUniqueWork(
            Constants.AIRPLANE_WORKER,
            ExistingWorkPolicy.KEEP,
            oneTimeWorkRequest,
        )
    }

    fun existingSearchAirplane(query: String): LiveData<List<Airplane>> = airplaneRepository.existingSearchAirplane(query)
    fun searchAirplane(query: String): LiveData<List<Airplane>> = airplaneRepository.searchAirplane(query)

    fun getAllAirlineFromDB(): LiveData<List<Airplane>> {
        return airplaneRepository.getAllAirplane()
    }

    fun insertAirlineFromDB(airplane: List<Airplane>) {
        viewModelScope.launch{
            airplaneRepository.insertAirplane(airplane)
        }
    }

    fun deleteAllAirlineFromDB(){
        viewModelScope.launch {
            airplaneRepository.deleteAllAirplane()
        }
    }

    fun deleteSingleAirlineFromDB(airplane: Airplane){
        viewModelScope.launch {
            airplaneRepository.deleteSingleAirplane(airplane)
        }
    }

    fun updateAirlineFromDB(airplane: Airplane){
        viewModelScope.launch {
            airplaneRepository.updateAirplane(airplane)
        }
    }

    fun addAirplanePrefs(airplane: Airplane){
        viewModelScope.launch { prefRepo.saveDataAirplane(airplane) }
    }

//    fun clearUserPref(){
//        viewModelScope.launch { prefRepo.clearDataDepartArrive() }
//    }

}