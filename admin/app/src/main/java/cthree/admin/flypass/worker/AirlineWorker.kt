package cthree.admin.flypass.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import cthree.admin.flypass.api.APIService
import cthree.admin.flypass.dao.AirlineDao
import cthree.admin.flypass.db.MyDatabase
import cthree.admin.flypass.models.airline.Airline
import cthree.admin.flypass.models.airline.GetAirlineResponse
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG = "AirlineWorker"

@HiltWorker
class AirlineWorker @AssistedInject constructor(
    @Assisted context : Context,
    @Assisted workerParams : WorkerParameters,
    private val apiService: APIService,
    private val airlineDao: AirlineDao,
): CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        return try {
            apiService.apiServiceAirline().enqueue(object : Callback<GetAirlineResponse> {
                override fun onResponse(call: Call<GetAirlineResponse>, response: Response<GetAirlineResponse>) {
                    if(response.isSuccessful){
                        response.body()?.let {
                            syncAirlineDB(it.airlines)
                        }
                    }else{
                        throw Exception("Failed")
                    }
                }

                override fun onFailure(call: Call<GetAirlineResponse>, t: Throwable) {
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

    fun syncAirlineDB(list: List<Airline>){
        MyDatabase.databaseWriteExecutor.execute {
            airlineDao.insertAirline(list)
        }
    }
}