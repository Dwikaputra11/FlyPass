package cthree.user.flypass.models.user


import com.google.gson.annotations.SerializedName

data class UpdateProfileResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("userId")
    val userId: Int
)