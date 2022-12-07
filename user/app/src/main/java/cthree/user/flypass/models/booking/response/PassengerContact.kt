package cthree.user.flypass.models.booking.response


import com.google.gson.annotations.SerializedName

data class PassengerContact(
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("firstName")
    val firstName: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("lastName")
    val lastName: String,
    @SerializedName("phone")
    val phone: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("updatedAt")
    val updatedAt: String
)