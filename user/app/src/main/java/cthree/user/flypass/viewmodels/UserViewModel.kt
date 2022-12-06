package cthree.user.flypass.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import cthree.user.flypass.api.ApiService
import cthree.user.flypass.models.login.Login
import cthree.user.flypass.models.login.LoginData
import cthree.user.flypass.models.user.Profile
import cthree.user.flypass.models.user.RegisterResponse
import cthree.user.flypass.models.user.UpdateProfile
import cthree.user.flypass.models.user.User
import cthree.user.flypass.preferences.UserPreferenceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

private const val TAG = "UserViewModel"

@HiltViewModel
class UserViewModel @Inject constructor(
    private val apiService: ApiService,
    application: Application
) : ViewModel() {

    private val prefRepo = UserPreferenceRepository(application.applicationContext)
    val dataUser = prefRepo.readData.asLiveData()

    private val tokenUser: MutableLiveData<String?> = MutableLiveData()
    private val liveDataUser : MutableLiveData<User?> = MutableLiveData()
    private val registerDataUser : MutableLiveData<RegisterResponse?> = MutableLiveData()
    private val errorMsg: MutableLiveData<String?> = MutableLiveData()

    fun getLoginToken(): LiveData<String?> = tokenUser

    fun getErrorMessage(): LiveData<String?> = errorMsg

    fun getLiveDataUser() : MutableLiveData<User?>{
        return liveDataUser
    }

    fun registerDataUser() : MutableLiveData<RegisterResponse?>{
        return registerDataUser
    }

    fun callLoginUser(loginData: LoginData){
        apiService.loginUser(loginData).enqueue(object : Callback<Login>{
            override fun onResponse(call: Call<Login>, response: Response<Login>) {
                if(response.isSuccessful){
                    response.body()?.let {
                        tokenUser.postValue(it.userLogin.accesstToken)
                    }
                }else{
                    tokenUser.postValue(null)
                    val jsonObject = JSONObject(response.errorBody()!!.charStream().readText())
                    errorMsg.postValue(jsonObject.getString("message"))
                    Log.d(TAG, "onResponse Call Login: ${jsonObject.getString("message")}")
                }
            }

            override fun onFailure(call: Call<Login>, t: Throwable) {
                tokenUser.postValue(null)
                Log.e(TAG, "onFailure Call Login: ${t.localizedMessage}")
            }

        })
    }

    fun callApiUser(){
        apiService.apiServiceUser()
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
        apiService.registerUser(profile)
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

    fun saveDataId(id : Int){
        viewModelScope.launch { prefRepo.saveDataUserId(id) }
    }
}