package cthree.user.flypass.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cthree.user.flypass.api.ApiService
import cthree.user.flypass.models.notification.NotificationList
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG = "NotificationViewModel"

@HiltViewModel
class NotificationViewModel constructor(private val apiService: ApiService): ViewModel() {

    val notificationList: MutableLiveData<NotificationList?> = MutableLiveData()

    fun getNotification(): LiveData<NotificationList?> = notificationList

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

}