package cthree.admin.flypass.models.admin


import com.google.gson.annotations.SerializedName

data class UserAdmin(
    @SerializedName("accesstToken")
    val accesstToken: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("id")
    val id: Int
)