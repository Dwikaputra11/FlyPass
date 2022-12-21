package cthree.user.flypass.data


import com.google.gson.annotations.SerializedName

data class UserData(
    @SerializedName("birthDate")
    val birthDate: String?,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("encryptedPassword")
    val encryptedPassword: String?,
    @SerializedName("gender")
    val gender: String?,
    @SerializedName("googleId")
    val googleId: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("image")
    val image: String,
    @SerializedName("imageId")
    val imageId: String?,
    @SerializedName("name")
    val name: String,
    @SerializedName("phone")
    val phone: String?,
    @SerializedName("refreshToken")
    val refreshToken: String,
    @SerializedName("roleId")
    val roleId: Int,
    @SerializedName("updatedAt")
    val updatedAt: String
)