package cthree.user.flypass.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cthree.user.flypass.api.ApiService
import cthree.user.flypass.models.wishlist.delete.DeleteWishlist
import cthree.user.flypass.models.wishlist.get.WishList
import cthree.user.flypass.models.wishlist.post.WishlistResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

private const val TAG = "WishlistViewModel"

@HiltViewModel
class WishlistViewModel @Inject constructor(private val apiService: ApiService): ViewModel() {

    private val postWishlistResp    : MutableLiveData<WishlistResponse>     = MutableLiveData()
    private val allWishlist         : MutableLiveData<WishList>             = MutableLiveData()
    private val deleteWishlist      : MutableLiveData<DeleteWishlist>       = MutableLiveData()

    fun postWishlistResponse()      : LiveData<WishlistResponse>    = postWishlistResp
    fun getAllWishlist()            : LiveData<WishList>            = allWishlist
    fun deleteWishlistResponse()    : LiveData<DeleteWishlist>      = deleteWishlist

    fun postWishlist(token: String, idFlight: Int){
        apiService.addWishlist(token, idFlight).enqueue(object : Callback<WishlistResponse>{
            override fun onResponse(
                call: Call<WishlistResponse>,
                response: Response<WishlistResponse>
            ) {
                if(response.isSuccessful){
                    postWishlistResp.postValue(response.body())
                }
            }

            override fun onFailure(call: Call<WishlistResponse>, t: Throwable) {
                Log.e(TAG, "postWishlist Failure: ${t.localizedMessage}")
            }
        })
    }

    fun getUserWishlist(token: String){
        apiService.getUserWishlist(token).enqueue(object : Callback<WishList>{
            override fun onResponse(call: Call<WishList>, response: Response<WishList>) {
                if(response.isSuccessful){
                    allWishlist.postValue(response.body())
                }
            }

            override fun onFailure(call: Call<WishList>, t: Throwable) {
                Log.e(TAG, "getAllWishlist Failure: ${t.localizedMessage}")
            }
        })
    }

    fun deleteWishlist(token: String, id: Int){
        apiService.deleteWishlist(token, id).enqueue(object : Callback<DeleteWishlist>{
            override fun onResponse(
                call: Call<DeleteWishlist>,
                response: Response<DeleteWishlist>
            ) {
                if(response.isSuccessful){
                    deleteWishlist.postValue(response.body())
                }
            }

            override fun onFailure(call: Call<DeleteWishlist>, t: Throwable) {
                Log.e(TAG, "deleteWishlist Failure: ${t.localizedMessage}")
            }
        })
    }
}