package cthree.user.flypass.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import cthree.user.flypass.api.ApiService
import cthree.user.flypass.models.login.Login
import cthree.user.flypass.models.login.LoginData
import cthree.user.flypass.models.user.*
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

    private val tokenUser: MutableLiveData<String?>                     = MutableLiveData()
    private val liveDataUser : MutableLiveData<User?>                   = MutableLiveData()
    private val registerDataUser : MutableLiveData<RegisterResponse?>   = MutableLiveData()
    private val loginErrorMsg: MutableLiveData<String?>                 = MutableLiveData()
    private val registErrorMsg: MutableLiveData<String?>                = MutableLiveData()

    fun getLoginToken(): LiveData<String?> = tokenUser

    fun getLoginErrorMessage(): LiveData<String?> = loginErrorMsg

    fun getRegisterErrorMessage(): LiveData<String?> = registErrorMsg

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
                    loginErrorMsg.postValue(jsonObject.getString("message"))
                    Log.d(TAG, "onResponse Call Login: ${jsonObject.getString("message")}")
                }
            }

            override fun onFailure(call: Call<Login>, t: Throwable) {
                tokenUser.postValue(null)
                Log.e(TAG, "onFailure Call Login: ${t.localizedMessage}")
            }

        })
    }

    fun registerUser(user: RegisterUser){
        apiService.registerUser(user).enqueue(object : Callback<RegisterResponse>{
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                if(response.isSuccessful){
                    registerDataUser.postValue(response.body())
                }else{
                    tokenUser.postValue(null)
                    val jsonObject = JSONObject(response.errorBody()!!.charStream().readText())
                    registErrorMsg.postValue(jsonObject.getString("message"))
                    Log.d(TAG, "onResponse Call Login: ${jsonObject.getString("message")}")
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                registErrorMsg.postValue(null)
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

    fun saveData(profile: Profile){
        viewModelScope.launch { prefRepo.saveDataUser(profile) }
    }

    fun clearAirportSearch(){
        viewModelScope.launch { prefRepo.clearDataDepartArrive() }
    }

    fun saveDataId(id : Int){
        viewModelScope.launch { prefRepo.saveDataUserId(id) }
    }

    fun saveLoginData(profile: Profile){
        viewModelScope.launch { prefRepo.saveLoginData(profile) }
    }
}