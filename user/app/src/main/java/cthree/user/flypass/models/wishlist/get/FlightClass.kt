package cthree.user.flypass.models.wishlist.get


import com.google.gson.annotations.SerializedName

data class FlightClass(
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("updatedAt")
    val updatedAt: String
)