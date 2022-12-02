package cthree.user.flypass.models.user


import com.google.gson.annotations.SerializedName

data class Profile(
    @SerializedName("email")
    val email: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("image")
    val image: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("phone")
    val phone: String,
    @SerializedName("roleId")
    val roleId: Int
)