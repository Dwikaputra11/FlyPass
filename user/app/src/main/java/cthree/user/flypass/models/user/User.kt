package cthree.user.flypass.models.user


import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("profile")
    val profile: Profile
)