package cthree.admin.flypass.viewmodels

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import cthree.admin.flypass.api.APIClient
import cthree.admin.flypass.models.admin.AdminDataClass
import cthree.admin.flypass.models.admin.LoginAdminResponse
import cthree.admin.flypass.models.admin.User
import cthree.admin.flypass.preferences.UserPreferenceRepository
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminViewModel constructor(application: Application): ViewModel() {

    private val prefRepo = UserPreferenceRepository(application.applicationContext)
    val dataAdmin = prefRepo.readData.asLiveData()

    private val postDataAdmin : MutableLiveData<User?> = MutableLiveData()

    fun postDataAdmin() : MutableLiveData<User?>{
        return postDataAdmin
    }

    fun callPostApiAdmin(email : String, password : String){
        APIClient.instance.loginAdmin(AdminDataClass(email, password))
            .enqueue(object : Callback<User> {
                override fun onResponse(
                    call: Call<User>,
                    response: Response<User>
                ) {
                    if (response.isSuccessful){
                        postDataAdmin.postValue(response.body())
                    }else{
                        postDataAdmin.postValue(null)
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    postDataAdmin.postValue(null)
                }

            })
    }

    fun saveData(admin: User){
        viewModelScope.launch { prefRepo.saveDataAdmin(admin) }
    }

//    fun saveDataId(id : Int){
//        viewModelScope.launch { prefRepo.saveDataUserId(id) }
//    }

    fun saveLoginStatus(status : Boolean){
        viewModelScope.launch { prefRepo.saveLoginStatus(status) }
    }

}