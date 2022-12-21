package cthree.user.flypass.models.user


import com.google.gson.annotations.SerializedName

data class Profile(
    @SerializedName("birthDate")
    var birthDate: String?,
    @SerializedName("email")
    var email: String,
    @SerializedName("gender")
    val gender: String?,
    @SerializedName("id")
    val id: Int,
    @SerializedName("image")
    var image: String?,
    @SerializedName("name")
    var name: String,
    @SerializedName("phone")
    var phone: String?,
    @SerializedName("roleId")
    val roleId: Int
)