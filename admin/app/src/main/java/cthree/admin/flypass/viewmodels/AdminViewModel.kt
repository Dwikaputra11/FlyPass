package cthree.admin.flypass.viewmodels

import android.app.Application
import androidx.lifecycle.*
import cthree.admin.flypass.api.APIService
import cthree.admin.flypass.models.admin.AdminDataClass
import cthree.admin.flypass.models.admin.LoginAdminResponse
import cthree.admin.flypass.models.admin.User
import cthree.admin.flypass.preferences.UserPreferenceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(private val apiService: APIService, application: Application): ViewModel() {

    private val prefRepo = UserPreferenceRepository(application.applicationContext)
    val dataAdmin = prefRepo.readData.asLiveData()

    private val tokenAdmin: MutableLiveData<String?> = MutableLiveData()
    private val loginErrorMsg: MutableLiveData<String?> = MutableLiveData()

    fun getLoginToken(): LiveData<String?> = tokenAdmin
    fun getLoginErrorMessage(): LiveData<String?> = loginErrorMsg


    fun loginAdmin(loginData: AdminDataClass){
        apiService.loginAdmin(loginData)
            .enqueue(object : Callback<LoginAdminResponse> {
                override fun onResponse(
                    call: Call<LoginAdminResponse>,
                    response: Response<LoginAdminResponse>
                ) {
                    if (response.isSuccessful){
                        response.body()?.let {
                            tokenAdmin.postValue(it.user.accesstToken)
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