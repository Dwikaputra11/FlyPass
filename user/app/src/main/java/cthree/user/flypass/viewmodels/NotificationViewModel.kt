package cthree.user.flypass.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cthree.user.flypass.api.ApiService
import cthree.user.flypass.models.notification.NotificationList
import cthree.user.flypass.models.notification.UpdateNotify
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

private const val TAG = "NotificationViewModel"

@HiltViewModel
class NotificationViewModel @Inject constructor(private val apiService: ApiService): ViewModel() {

    private val notificationList: MutableLiveData<NotificationList?> = MutableLiveData()
    val updateNotification: MutableLiveData<UpdateNotify?> = MutableLiveData()

    fun getNotification(): LiveData<NotificationList?> = notificationList
    fun getUpdateNotify(): LiveData<UpdateNotify?> = updateNotification

    fun callNotification(token: String){
        apiService.getNotification("Bearer $token").enqueue(object : Callback<NotificationList>{
            override fun onResponse(
                call: Call<NotificationList>,
                response: Response<NotificationList>
            ) {
                if(response.isSuccessful){
                    notificationList.postValue(response.body())
                }else{
                    Log.e(TAG, "Get Notif unsuccess")
                    notificationList.postValue(null)
                }
            }

            override fun onFailure(call: Call<NotificationList>, t: Throwable) {
                Log.e(TAG, "Get Notif Failed: ${t.localizedMessage}")
            }

        })
    }

    fun updateNotification(token: String, id: Int){
        apiService.updateNotification("Bearer $token", id).enqueue(object : Callback<UpdateNotify>{
            override fun onResponse(call: Call<UpdateNotify>, response: Response<UpdateNotify>) {
                if(response.isSuccessful){
                    updateNotification.postValue(response.body())
                }else{
                    updateNotification.postValue(null)
                }
            }

            override fun onFailure(call: Call<UpdateNotify>, t: Throwable) {
                updateNotification.postValue(null)
                Log.d(TAG, "onFailure: ${t.localizedMessage}")
            }

        })
    }

}