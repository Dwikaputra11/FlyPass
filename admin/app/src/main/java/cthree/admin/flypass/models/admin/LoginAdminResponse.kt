package cthree.admin.flypass.models.admin


import com.google.gson.annotations.SerializedName

data class LoginAdminResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("user")
    val user: User
)