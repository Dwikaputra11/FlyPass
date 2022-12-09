package cthree.user.flypass.models.wishlist.post


import com.google.gson.annotations.SerializedName

data class Wishlist(
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("flightId")
    val flightId: Int,
    @SerializedName("id")
    val id: Int,
    @SerializedName("updatedAt")
    val updatedAt: String,
    @SerializedName("userId")
    val userId: Int
)