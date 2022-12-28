package cthree.user.flypass.models.booking.bookings


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class FlightType(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String
): Parcelable