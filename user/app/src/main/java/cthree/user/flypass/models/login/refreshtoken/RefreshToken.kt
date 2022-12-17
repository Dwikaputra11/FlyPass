package cthree.user.flypass.models.login.refreshtoken


import com.google.gson.annotations.SerializedName

data class RefreshToken(
    @SerializedName("accessToken")
    val accessToken: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("userId")
    val userId: Int
)