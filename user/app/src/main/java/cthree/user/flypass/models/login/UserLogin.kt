package cthree.user.flypass.models.login


import com.google.gson.annotations.SerializedName

data class UserLogin(
    @SerializedName("accesstToken")
    val accesstToken: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("id")
    val id: Int
)