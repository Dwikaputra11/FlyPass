package cthree.user.flypass.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import cthree.user.flypass.api.ApiService
import cthree.user.flypass.models.user.Profile
import cthree.user.flypass.preferences.UserPreferenceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PreferencesViewModel @Inject constructor(
    private val apiService: ApiService,
    application: Application
) : ViewModel(){
    private val prefRepo = UserPreferenceRepository(application.applicationContext)
    val dataUser = prefRepo.readData.asLiveData()

    fun saveBookingData(depFlight: String, arrFlight: String?, passList: String, contactDetail: String, baggageList: String){
        viewModelScope.launch { prefRepo.saveBookingData(depFlight, arrFlight, passList, contactDetail, baggageList) }
    }

    fun savePaymentData(bookingCode: String,addOnsPrice: Int, depFlightPrice: Int,arrFlightPrice: Int?, totalPrice: Int){
        viewModelScope.launch { prefRepo.savePaymentData(bookingCode, addOnsPrice, depFlightPrice, arrFlightPrice, totalPrice) }
    }

    fun clearBookingData(){
        viewModelScope.launch { prefRepo.clearBookingData() }
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

    fun savePinMember(pinMember: String){
        viewModelScope.launch { prefRepo.savePinMember(pinMember) }
    }

    fun clearPinMember(){
        viewModelScope.launch { prefRepo.clearPinMember() }
    }

    fun clearAirportSearch(){
        viewModelScope.launch { prefRepo.clearDataDepartArrive() }
    }

    fun saveLoginData(profile: Profile){
        viewModelScope.launch { prefRepo.saveLoginData(profile) }
    }

}