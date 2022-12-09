package cthree.user.flypass.models.wishlist.get


import com.google.gson.annotations.SerializedName

data class Airplane(
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("icao")
    val icao: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("model")
    val model: String,
    @SerializedName("updatedAt")
    val updatedAt: String
)