package cthree.admin.flypass.models.user


import com.google.gson.annotations.SerializedName

data class GetUserResponse(
    @SerializedName("users")
    val users: List<User>
)