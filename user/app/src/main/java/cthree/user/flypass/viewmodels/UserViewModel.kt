package cthree.user.flypass.viewmodels

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import cthree.user.flypass.api.APIClient
import cthree.user.flypass.models.user.Profile
import cthree.user.flypass.models.user.UpdateProfile
import cthree.user.flypass.models.user.RegisterResponse
import cthree.user.flypass.models.user.User
import cthree.user.flypass.preferences.UserPreferenceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(application: Application) : ViewModel() {

    private val prefRepo = UserPreferenceRepository(application.applicationContext)
    val dataUser = prefRepo.readData.asLiveData()

    private val liveDataUser : MutableLiveData<User?> = MutableLiveData()
    private val registerDataUser : MutableLiveData<RegisterResponse?> = MutableLiveData()

    fun getLiveDataUser() : MutableLiveData<User?>{
        return liveDataUser
    }

    fun registerDataUser() : MutableLiveData<RegisterResponse?>{
        return registerDataUser
    }

    fun callApiUser(){
        APIClient.instance.apiServiceUser()
            .enqueue(object : Callback<User> {
                override fun onResponse(
                    call: Call<User>,
                    response: Response<User>
                ) {
                    if (response.isSuccessful){
                        liveDataUser.postValue(response.body())
                    } else{
                        liveDataUser.postValue(null)
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    liveDataUser.postValue(null)
                }
            })
    }

    fun callPostApiUser(profile : UpdateProfile){
        APIClient.instance.registerUser(profile)
            .enqueue(object : Callback<RegisterResponse>{
                override fun onResponse(
                    call: Call<RegisterResponse>,
                    response: Response<RegisterResponse>
                ) {
                    if (response.isSuccessful){
                        registerDataUser.postValue(response.body())
                    }else{
                        registerDataUser.postValue(null)
                    }
                }

                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                    registerDataUser.postValue(null)
                }

            })
    }

    fun saveData(profile: Profile){
        viewModelScope.launch { prefRepo.saveDataUser(profile) }
    }

//    fun saveDataId(id : Int){
//        viewModelScope.launch { prefRepo.saveDataUserId(id) }
//    }

    fun saveLoginStatus(status : Boolean){
        viewModelScope.launch { prefRepo.saveLoginStatus(status) }
    }
}