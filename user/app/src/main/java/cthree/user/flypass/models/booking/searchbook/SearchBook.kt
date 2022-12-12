package cthree.user.flypass.models.booking.searchbook


import com.google.gson.annotations.SerializedName

data class SearchBook(
    @SerializedName("booking")
    val booking: List<Booking>
)