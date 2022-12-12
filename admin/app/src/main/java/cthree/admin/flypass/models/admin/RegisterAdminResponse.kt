package cthree.admin.flypass.models.admin


import com.google.gson.annotations.SerializedName

data class RegisterAdminResponse(
    @SerializedName("message")
    val message: String
)