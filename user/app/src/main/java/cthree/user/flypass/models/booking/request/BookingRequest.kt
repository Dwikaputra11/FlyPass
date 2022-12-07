package cthree.user.flypass.models.booking.request


import com.google.gson.annotations.SerializedName

data class BookingRequest(
    @SerializedName("contactEmail")
    val contactEmail: String,
    @SerializedName("contactFirstName")
    val contactFirstName: String,
    @SerializedName("contactLastName")
    val contactLastName: String,
    @SerializedName("contactPhone")
    val contactPhone: String,
    @SerializedName("contactTitle")
    val contactTitle: String,
    @SerializedName("flight1Id")
    val flight1Id: String,
    @SerializedName("flight2Id")
    val flight2Id: String,
    @SerializedName("passenger")
    val passenger: List<Passenger>
)