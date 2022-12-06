package cthree.user.flypass.models.login


import com.google.gson.annotations.SerializedName

data class Login(
    @SerializedName("message")
    val message: String,
    @SerializedName("user")
    val userLogin: UserLogin
)