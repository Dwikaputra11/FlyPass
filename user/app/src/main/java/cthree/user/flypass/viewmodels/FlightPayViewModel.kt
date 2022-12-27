package cthree.user.flypass.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cthree.user.flypass.api.ApiService
import cthree.user.flypass.data.BalanceRequestAmount
import cthree.user.flypass.data.InputPinMember
import cthree.user.flypass.models.flightpay.ActivateEWalletResponse
import cthree.user.flypass.models.flightpay.UserWallet
import cthree.user.flypass.models.flightpay.history.HistoryTopupList
import cthree.user.flypass.models.flightpay.topup.TopupConfirmation
import cthree.user.flypass.models.flightpay.topup.TopupRequestResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject


private const val TAG = "FlightPayViewModel"
@HiltViewModel
class FlightPayViewModel @Inject constructor(private val apiService: ApiService): ViewModel() {

    private val activateWallet: MutableLiveData<ActivateEWalletResponse?> = MutableLiveData()
    private val userWallet: MutableLiveData<UserWallet?> = MutableLiveData()
    private val topUpRequest: MutableLiveData<TopupRequestResponse?> = MutableLiveData()
    private val topUpConfirm: MutableLiveData<TopupConfirmation?> = MutableLiveData()
    private val walletHistory: MutableLiveData<HistoryTopupList?> = MutableLiveData()
    private val requestStatusCode: MutableLiveData<Int?> = MutableLiveData()

    fun getActivateWallet(): LiveData<ActivateEWalletResponse?> = activateWallet
    fun getUserWallet(): LiveData<UserWallet?> = userWallet
    fun getTopUpRequest(): LiveData<TopupRequestResponse?> = topUpRequest
    fun getTopUpConfirm(): LiveData<TopupConfirmation?> = topUpConfirm
    fun walletHistory(): LiveData<HistoryTopupList?> = walletHistory
    fun getRequestCode(): LiveData<Int?> = requestStatusCode


    fun activateWallet(pin: InputPinMember, token: String){
        apiService.activateEWallet(pin, "Bearer $token").enqueue(object : Callback<ActivateEWalletResponse>{
            override fun onResponse(
                call: Call<ActivateEWalletResponse>,
                response: Response<ActivateEWalletResponse>
            ) {
                if(response.isSuccessful){
                    activateWallet.postValue(response.body())
                }else{
                    activateWallet.postValue(null)
                }
            }

            override fun onFailure(call: Call<ActivateEWalletResponse>, t: Throwable) {
                Log.e(TAG, "Activate wallet: ${t.localizedMessage}")
                activateWallet.postValue(null)
            }

        })
    }

    fun userWallet(token: String){
        apiService.userWallet("Bearer $token").enqueue(object : Callback<UserWallet>{
            override fun onResponse(call: Call<UserWallet>, response: Response<UserWallet>) {
                requestStatusCode.postValue(response.code())
                if(response.isSuccessful){
                    userWallet.postValue(response.body())
                }
            }

            override fun onFailure(call: Call<UserWallet>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }

    fun topUpRequest(token: String, amount: BalanceRequestAmount){
        apiService.topUpRequest("Bearer $token", amount).enqueue(object : Callback<TopupRequestResponse>{
            override fun onResponse(
                call: Call<TopupRequestResponse>,
                response: Response<TopupRequestResponse>
            ) {
                if(response.isSuccessful){
                    topUpRequest.postValue(response.body())
                }
            }

            override fun onFailure(call: Call<TopupRequestResponse>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }

    fun topUpConfirmation(walletHistoryId: Int, token: String){
        apiService.topUpConfirm("Bearer $token", walletHistoryId).enqueue(object : Callback<TopupConfirmation>{
            override fun onResponse(
                call: Call<TopupConfirmation>,
                response: Response<TopupConfirmation>
            ) {
                if(response.isSuccessful){
                    topUpConfirm.postValue(response.body())
                }
            }

            override fun onFailure(call: Call<TopupConfirmation>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }

    fun walletHistory(token: String){
        apiService.walletHistory("Bearer $token").enqueue(object : Callback<HistoryTopupList>{
            override fun onResponse(
                call: Call<HistoryTopupList>,
                response: Response<HistoryTopupList>
            ) {
                requestStatusCode.postValue(response.code())
                if(response.isSuccessful){
                    walletHistory.postValue(response.body())
                }
            }

            override fun onFailure(call: Call<HistoryTopupList>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }
}