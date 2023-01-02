package cthree.admin.flypass.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import cthree.admin.flypass.api.APIService
import cthree.admin.flypass.dao.AirplaneDao
import cthree.admin.flypass.db.MyDatabase
import cthree.admin.flypass.models.airplane.Airplane
import cthree.admin.flypass.models.airplane.GetAirplaneResponse
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG = "AirplaneWorker"
@HiltWorker
class AirplaneWorker @AssistedInject constructor(
    @Assisted context : Context,
    @Assisted workerParams : WorkerParameters,
    private val apiService: APIService,
    private val airplaneDao: AirplaneDao,
) : CoroutineWorker(context, workerParams){
    override suspend fun doWork(): Result {
        return try {
            apiService.apiServiceAirplane().enqueue(object : Callback<GetAirplaneResponse> {
                override fun onResponse(call: Call<GetAirplaneResponse>, response: Response<GetAirplaneResponse>) {
                    if(response.isSuccessful){
                        response.body()?.let {
                            syncAirplaneDB(it.airplane)
                        }
                    }else{
                        throw Exception("Failed")
                    }
                }

                override fun onFailure(call: Call<GetAirplaneResponse>, t: Throwable) {
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

    fun syncAirplaneDB(list: List<Airplane>){
        MyDatabase.databaseWriteExecutor.execute {
            airplaneDao.insertAirplane(list)
        }
    }

}