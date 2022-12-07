package cthree.admin.flypass.viewmodels

import android.app.Application
import androidx.lifecycle.*
import cthree.admin.flypass.api.APIService
import cthree.admin.flypass.models.admin.AdminDataClass
import cthree.admin.flypass.models.admin.User
import cthree.admin.flypass.preferences.UserPreferenceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(private val apiService: APIService, application: Application): ViewModel() {

    private val prefRepo = UserPreferenceRepository(application.applicationContext)
    val dataAdmin = prefRepo.readData.asLiveData()

    private val postDataAdmin : MutableLiveData<User?> = MutableLiveData()
    private val tokenAdmin: MutableLiveData<String?> = MutableLiveData()
    private val errorMsg: MutableLiveData<String?> = MutableLiveData()

    fun getLoginToken(): LiveData<String?> = tokenAdmin

    fun getErrorMessage(): LiveData<String?> = errorMsg

    fun postDataAdmin() : MutableLiveData<User?>{
        return postDataAdmin
    }

    fun loginAdmin(email : String, password : String){
        apiService.loginAdmin(AdminDataClass(email, password))
            .enqueue(object : Callback<User> {
                override fun onResponse(
                    call: Call<User>,
                    response: Response<User>
                ) {
                    if (response.isSuccessful){
                        response.body()?.let {
                            tokenAdmin.postValue(it.accesstToken)
                        }
                    }else{
                        tokenAdmin.postValue(null)
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
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