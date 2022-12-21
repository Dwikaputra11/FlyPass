package cthree.admin.flypass.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import cthree.admin.flypass.api.APIService
import cthree.admin.flypass.models.admin.*
import cthree.admin.flypass.models.ticketflight.Flight
import cthree.admin.flypass.models.ticketflight.GetTicketResponse
import cthree.admin.flypass.models.user.GetUserResponse
import cthree.admin.flypass.models.user.User
import cthree.admin.flypass.preferences.UserPreferenceRepository
import cthree.admin.flypass.utils.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

private const val TAG = "AdminViewModel"

@HiltViewModel
class AdminViewModel @Inject constructor(private val apiService: APIService, application: Application): ViewModel() {

    private val prefRepo = UserPreferenceRepository(application.applicationContext)
    val dataAdmin = prefRepo.readData.asLiveData()

    private val tokenAdmin: MutableLiveData<String?> = MutableLiveData()
    private val loginErrorMsg: MutableLiveData<String?> = MutableLiveData()
    private val registerErrorMsg: MutableLiveData<String?> = MutableLiveData()
    private val liveDataUser: MutableLiveData<GetUserResponse?> = MutableLiveData()
    private val postRegisterAdmin: MutableLiveData<RegisterAdminResponse?> = MutableLiveData()

    fun getLoginToken(): LiveData<String?> = tokenAdmin
    fun getLoginErrorMessage(): LiveData<String?> = loginErrorMsg
    fun getRegisterErrorMessage(): LiveData<String?> = registerErrorMsg

    fun getLiveDataUsers() : MutableLiveData<GetUserResponse?> {
        return liveDataUser
    }

    fun postRegisterAdmin(): MutableLiveData<RegisterAdminResponse?> {
        return postRegisterAdmin
    }

    fun loginAdmin(loginData: AdminDataClass){
        apiService.loginAdmin(loginData)
            .enqueue(object : Callback<LoginAdminResponse> {
                override fun onResponse(
                    call: Call<LoginAdminResponse>,
                    response: Response<LoginAdminResponse>
                ) {
                    if (response.isSuccessful){
                        response.body()?.let {
                            tokenAdmin.postValue(it.userAdmin.accesstToken)
                        }
                    }else{
                        tokenAdmin.postValue(null)
                        val jsonObject = JSONObject(response.errorBody()!!.charStream().readText())
                        loginErrorMsg.postValue(jsonObject.getString("message"))
                    }
                }

                override fun onFailure(call: Call<LoginAdminResponse>, t: Throwable) {
                    tokenAdmin.postValue(null)
                }

            })
    }

    fun registerAdmin(token: String, registerData: RegisterAdminDataClass){
        apiService.registerAdmin(token, registerData)
            .enqueue(object : Callback<RegisterAdminResponse> {
                override fun onResponse(
                    call: Call<RegisterAdminResponse>,
                    response: Response<RegisterAdminResponse>
                ) {
                    if (response.isSuccessful){
                        postRegisterAdmin.postValue(response.body())
                    }else{
                        postRegisterAdmin.postValue(null)
                        val jsonObject = JSONObject(response.errorBody()!!.charStream().readText())
                        registerErrorMsg.postValue(jsonObject.getString("message"))
                    }
                }

                override fun onFailure(call: Call<RegisterAdminResponse>, t: Throwable) {
                    postRegisterAdmin.postValue(null)
                }

            })
    }

    fun callApiUser(token : String){
        Log.d(TAG, "callApiUser: $token")
        apiService.getAllUser(token)
            .enqueue(object : Callback<GetUserResponse> {
                override fun onResponse(
                    call: Call<GetUserResponse>,
                    response: Response<GetUserResponse>
                ) {
                    if (response.isSuccessful){
                        liveDataUser.postValue(response.body())
                    } else{
                        liveDataUser.postValue(null)
                    }
                }

                override fun onFailure(call: Call<GetUserResponse>, t: Throwable) {
                    liveDataUser.postValue(null)
                }
            })
    }

    fun saveData(admin: UserAdmin){
        viewModelScope.launch { prefRepo.saveDataAdmin(admin) }
    }

//    fun saveDataId(id : Int){
//        viewModelScope.launch { prefRepo.saveDataUserId(id) }
//    }

    fun saveLoginStatus(status : Boolean){
        viewModelScope.launch { prefRepo.saveLoginStatus(status) }
    }

}