package cthree.user.flypass.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import cthree.user.flypass.api.ApiService
import cthree.user.flypass.data.GoogleTokenRequest
import cthree.user.flypass.data.UpdateProfile
import cthree.user.flypass.models.login.Login
import cthree.user.flypass.models.login.LoginData
import cthree.user.flypass.models.login.refreshtoken.RefreshToken
import cthree.user.flypass.models.user.*
import cthree.user.flypass.preferences.UserPreferenceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
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

    private val tokenUser           : MutableLiveData<String?>                  = MutableLiveData()
    private val userProfile         : MutableLiveData<User?>                    = MutableLiveData()
    private val registerDataUser    : MutableLiveData<RegisterResponse?>        = MutableLiveData()
    private val loginErrorMsg       : MutableLiveData<String?>                  = MutableLiveData()
    private val registErrorMsg      : MutableLiveData<String?>                  = MutableLiveData()
    private val updatePhotoProfile  : MutableLiveData<UpdateProfileResponse?>   = MutableLiveData()
    private val updateProfile       : MutableLiveData<UpdateProfileResponse?>   = MutableLiveData()
    private val refreshToken        : MutableLiveData<String?>                  = MutableLiveData()
    private val logoutUser          : MutableLiveData<Int?>                     = MutableLiveData()
    private val accessToken         : MutableLiveData<RefreshToken?>            = MutableLiveData()
    private val googleIdTokenLogin  : MutableLiveData<Login?>                   = MutableLiveData()

    fun loginToken()                : LiveData<String?>                 = tokenUser
    fun getLoginErrorMessage()      : LiveData<String?>                 = loginErrorMsg
    fun getRegisterErrorMessage()   : LiveData<String?>                 = registErrorMsg
    fun getUserProfile()            : LiveData<User?>                   = userProfile
    fun registerDataUser()          : LiveData<RegisterResponse?>       = registerDataUser
    fun getUpdatePhotoProfile()     : LiveData<UpdateProfileResponse?>  = updatePhotoProfile
    fun getUpdateProfile()          : LiveData<UpdateProfileResponse?>  = updateProfile
    fun getRefreshToken()           : LiveData<String?>                 = refreshToken
    fun getLogoutStatus()           : LiveData<Int?>                    = logoutUser
    fun getAccessToken()            : LiveData<RefreshToken?>           = accessToken
    fun getGoogleIdTokenLogin()     : LiveData<Login?>                  = googleIdTokenLogin

    fun refreshToken(token: String){
        apiService.refreshToken("Bearer $token").enqueue(object : Callback<RefreshToken>{
            override fun onResponse(call: Call<RefreshToken>, response: Response<RefreshToken>) {
                if(response.isSuccessful){
                    response.body()?.let {
                        accessToken.postValue(it)
                    }
                }else{
                    accessToken.postValue(null)
//                    val jsonObject = JSONObject(response.errorBody()!!.charStream().readText())
//                    loginErrorMsg.postValue(jsonObject.getString("message"))
//                    Log.d(TAG, "onResponse Call Login: ${jsonObject.getString("message")}")
                }
            }

            override fun onFailure(call: Call<RefreshToken>, t: Throwable) {
                accessToken.postValue(null)
                Log.e(TAG, "onFailure Call Login: ${t.localizedMessage}")
            }

        })
    }

    fun callLoginUser(loginData: LoginData){
        apiService.loginUser(loginData).enqueue(object : Callback<Login>{
            override fun onResponse(call: Call<Login>, response: Response<Login>) {
                if(response.isSuccessful){
                    response.body()?.let {
                        tokenUser.postValue(it.userLogin.accesstToken)
                        Log.d(TAG, "Cookies: ${response.headers()["Set-Cookie"]?.split(";")?.get(0)?.substringAfter("=")}")
                        Log.d(TAG, "Cookies Value: ${response.headers().values("Set-Cookie")[0]}")
                    }
                    refreshToken.postValue(response.headers()["Set-Cookie"]?.split(";")?.get(0)?.substringAfter("=")?.trim())
                }else{
                    tokenUser.postValue(null)
                    val jsonObject = JSONObject(response.errorBody()!!.charStream().readText())
                    loginErrorMsg.postValue(jsonObject.getString("message"))
                    Log.d(TAG, "onResponse Call Login: ${jsonObject.getString("message")}")
                    refreshToken.postValue(null)
                }
            }

            override fun onFailure(call: Call<Login>, t: Throwable) {
                tokenUser.postValue(null)
                refreshToken.postValue(null)
                Log.e(TAG, "onFailure Call Login: ${t.localizedMessage}")
            }

        })
    }

    fun callGoogleIdTokenLogin(idToken: GoogleTokenRequest){
        apiService.googleIdTokenLogin(idToken).enqueue(object : Callback<Login>{
            override fun onResponse(call: Call<Login>, response: Response<Login>) {
                if(response.isSuccessful){
                    response.body()?.let {
                        tokenUser.postValue(it.userLogin.accesstToken)
                    }
                }else{
                    googleIdTokenLogin.postValue(null)
                    Log.d(TAG, "onResponse: ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<Login>, t: Throwable) {
                googleIdTokenLogin.postValue(null)
                Log.e(TAG, "googleIdToken: ${t.localizedMessage}")
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

    fun logoutUser(token: String){
        apiService.logout("Bearer $token").enqueue(object : Callback<String>{
            override fun onResponse(call: Call<String>, response: Response<String>) {
                logoutUser.postValue(response.code())
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                logoutUser.postValue(null)
                Log.d(TAG, "logoutUser: ${t.localizedMessage}")
            }

        })
    }

    fun updatePhotoProfile(
        token: String,
        name: RequestBody,
        email: RequestBody,
        phone: RequestBody?,
        image: MultipartBody.Part,
        gender: RequestBody?,
        birthDate: RequestBody?
    ){
        apiService.updatePhotoProfile("Bearer $token",name,email, phone, image, gender, birthDate).enqueue(object : Callback<UpdateProfileResponse>{
            override fun onResponse(
                call: Call<UpdateProfileResponse>,
                response: Response<UpdateProfileResponse>
            ) {
                if(response.isSuccessful){
                    updatePhotoProfile.postValue(response.body())
                }else{
                    updatePhotoProfile.postValue(null)
                    Log.d(TAG, "Update Photo Profile: Unsuccessfully")
                }
            }

            override fun onFailure(call: Call<UpdateProfileResponse>, t: Throwable) {
                updatePhotoProfile.postValue(null)
                Log.d(TAG, "Update Photo Profile: ${t.localizedMessage}")
            }

        })
    }

    fun updateProfile(token:String,profile: UpdateProfile){
        apiService.updateProfile("Bearer $token", profile).enqueue(object : Callback<UpdateProfileResponse>{
            override fun onResponse(
                call: Call<UpdateProfileResponse>,
                response: Response<UpdateProfileResponse>
            ) {
                if(response.isSuccessful){
                    updateProfile.postValue(response.body())
                }else{
                    updateProfile.postValue(null)
                    Log.d(TAG, "Update Profile: Unsuccessfully")
                }
            }

            override fun onFailure(call: Call<UpdateProfileResponse>, t: Throwable) {
                updateProfile.postValue(null)
                Log.d(TAG, "Update Profile: ${t.localizedMessage}")
            }

        })
    }

    fun callUserProfile(token: String){
        apiService.getUserProfile("Bearer $token")
            .enqueue(object : Callback<User> {
                override fun onResponse(
                    call: Call<User>,
                    response: Response<User>
                ) {
                    if (response.isSuccessful){
                        userProfile.postValue(response.body())
                    } else{
                        userProfile.postValue(null)
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    userProfile.postValue(null)
                }
            })
    }

    fun saveData(profile: Profile){
        viewModelScope.launch { prefRepo.saveDataUser(profile) }
    }

    fun clearProfileData(){
        viewModelScope.launch { prefRepo.clearDataUser()}
    }

    fun saveToken(token: String){
        viewModelScope.launch { prefRepo.saveToken(token) }
    }

    fun clearToken(){
        viewModelScope.launch { prefRepo.clearToken() }
    }

    fun saveRefreshToken(token: String){
        viewModelScope.launch { prefRepo.saveRefreshToken(token) }
    }

    fun clearRefreshToken(){
        viewModelScope.launch { prefRepo.clearRefreshToken() }
    }

    fun clearAirportSearch(){
        viewModelScope.launch { prefRepo.clearDataDepartArrive() }
    }

    fun saveLoginData(profile: Profile){
        viewModelScope.launch { prefRepo.saveLoginData(profile) }
    }
}