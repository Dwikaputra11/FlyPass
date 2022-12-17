package cthree.user.flypass.models.booking.bookings


import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Passenger(
    @SerializedName("age")
    val age: Int?,
    @SerializedName("baggage")
    val baggage: List<Int>,
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
) : Parcelable