package cthree.admin.flypass.models.ticketflight


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
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
    val imageId: String?,
    @SerializedName("name")
    val name: String,
    @SerializedName("updatedAt")
    val updatedAt: String
) : Parcelable