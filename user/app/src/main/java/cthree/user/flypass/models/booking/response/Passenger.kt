package cthree.user.flypass.models.booking.response


import com.google.gson.annotations.SerializedName

data class Passenger(
    @SerializedName("age")
    val age: Int?,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("firstName")
    val firstName: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("identityNumber")
    val identityNumber: String,
    @SerializedName("identityType")
    val identityType: String,
    @SerializedName("lastName")
    val lastName: String,
    @SerializedName("updatedAt")
    val updatedAt: String
)