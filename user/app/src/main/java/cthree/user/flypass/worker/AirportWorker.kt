package cthree.user.flypass.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import cthree.user.flypass.api.ApiService
import cthree.user.flypass.dao.AirportDao
import cthree.user.flypass.db.MyDatabase
import cthree.user.flypass.models.airport.Airport
import cthree.user.flypass.models.airport.AirportList
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG = "AirportWorker"

@HiltWorker
class AirportWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val apiService: ApiService,
    private val airportDao: AirportDao,
): CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            apiService.apiServiceAirport().enqueue(object : Callback<AirportList> {
                override fun onResponse(call: Call<AirportList>, response: Response<AirportList>) {
                    if(response.isSuccessful){
                        response.body()?.let {
                            syncAirportDB(it.airport)
                        }
                    }else{
                        throw Exception("Failed")
                    }
                }

                override fun onFailure(call: Call<AirportList>, t: Throwable) {
                    throw Exception(t.localizedMessage)
                }

            })

            Result.success()
        }catch (e: Exception){
            if(runAttemptCount <= 3){
                Log.d(TAG, "Error Message: ${e.localizedMessage}")
                Log.d(TAG, "doWork: Retry")
                Result.retry()
            }else{
                Log.d(TAG, "Error Message: ${e.localizedMessage}")
                Log.d(TAG, "doWork: Failed to get data source")
                Result.failure()
            }
        }
    }

    fun syncAirportDB(list: List<Airport>){
        list.forEach {
            if(it.iata == null){
                Log.d(TAG, "syncAirportDB iata: ${it.country}, ${it.name}, ${it.id}")
            }
        }
        MyDatabase.databaseWriteExecutor.execute {
            airportDao.insertAirport(list)
        }
    }
}