package cthree.user.flypass.models.booking.response


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class BookingResponse(
    @SerializedName("booking")
    val booking: Booking,
    @SerializedName("passenger")
    val passenger: List<Passenger>,
    @SerializedName("passengerBooking")
    val passengerBooking: List<PassengerBooking>,
    @SerializedName("passengerContact")
    val passengerContact: PassengerContact
): Parcelable