package cthree.user.flypass.models.booking.response


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class PassengerBooking(
    @SerializedName("bookingId")
    val bookingId: Int,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("passengerId")
    val passengerId: Int,
    @SerializedName("updatedAt")
    val updatedAt: String
): Parcelable