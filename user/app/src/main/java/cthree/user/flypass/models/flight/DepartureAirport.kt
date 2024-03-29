package cthree.user.flypass.models.flight


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class DepartureAirport(
    @SerializedName("city")
    val city: String,
    @SerializedName("country")
    val country: String,
    @SerializedName("iata")
    val iata: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String
): Parcelable