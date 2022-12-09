package cthree.user.flypass.models.wishlist.get


import com.google.gson.annotations.SerializedName

data class Airline(
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("iata")
    val iata: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("image")
    val image: String,
    @SerializedName("imageId")
    val imageId: Any,
    @SerializedName("name")
    val name: String,
    @SerializedName("updatedAt")
    val updatedAt: String
)