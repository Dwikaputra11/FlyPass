package cthree.user.flypass.models.booking.bookings


import com.google.gson.annotations.SerializedName

data class BookingListResponse(
    @SerializedName("booking")
    val booking: List<Booking>
)