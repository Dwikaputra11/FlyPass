package cthree.user.flypass.models.booking.bookings


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
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
) : Parcelable