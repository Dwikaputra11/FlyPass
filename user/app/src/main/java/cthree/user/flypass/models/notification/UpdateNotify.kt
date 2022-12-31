package cthree.user.flypass.models.notification


import com.google.gson.annotations.SerializedName

data class UpdateNotify(
    @SerializedName("message")
    val message: String
)