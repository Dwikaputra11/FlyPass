package cthree.user.flypass.models.booking.bookings


import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class Booking(
    @SerializedName("bookingCode")
    val bookingCode: String,
    @SerializedName("BookingStatus")
    val bookingStatus: BookingStatus,
    @SerializedName("bookingStatusId")
    val bookingStatusId: Int,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("flight1")
    val flightBook: FlightBook,
    @SerializedName("flight1Id")
    val flight1Id: Int,
    @SerializedName("flight2")
    val flight2: FlightBook?,
    @SerializedName("flight2Id")
    val flight2Id: Int?,
    @SerializedName("id")
    val id: Int,
    @SerializedName("PassengerContact")
    val passengerContact: PassengerContact,
    @SerializedName("passengerContactId")
    val passengerContactId: Int,
    @SerializedName("passengerQty")
    val passengerQty: Int,
    @SerializedName("Passengers")
    val passengers: List<Passenger>,
    @SerializedName("roundtrip")
    val roundtrip: Boolean,
    @SerializedName("totalPrice")
    val totalPrice: Int,
    @SerializedName("updatedAt")
    val updatedAt: String,
    @SerializedName("userId")
    val userId: Int?
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readParcelable(BookingStatus::class.java.classLoader)!!,
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readParcelable(FlightBook::class.java.classLoader)!!,
        parcel.readInt(),
        parcel.readParcelable(FlightBook::class.java.classLoader),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readInt(),
        parcel.readParcelable(PassengerContact::class.java.classLoader)!!,
        parcel.readInt(),
        parcel.readInt(),
        parcel.createTypedArrayList(Passenger)!!,
        parcel.readByte() != 0.toByte(),
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readValue(Int::class.java.classLoader) as? Int
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(bookingCode)
        parcel.writeParcelable(bookingStatus, flags)
        parcel.writeInt(bookingStatusId)
        parcel.writeString(createdAt)
        parcel.writeParcelable(flightBook, flags)
        parcel.writeInt(flight1Id)
        parcel.writeParcelable(flight2, flags)
        parcel.writeValue(flight2Id)
        parcel.writeInt(id)
        parcel.writeParcelable(passengerContact, flags)
        parcel.writeInt(passengerContactId)
        parcel.writeInt(passengerQty)
        parcel.writeTypedList(passengers)
        parcel.writeByte(if (roundtrip) 1 else 0)
        parcel.writeInt(totalPrice)
        parcel.writeString(updatedAt)
        parcel.writeValue(userId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Booking> {
        override fun createFromParcel(parcel: Parcel): Booking {
            return Booking(parcel)
        }

        override fun newArray(size: Int): Array<Booking?> {
            return arrayOfNulls(size)
        }
    }
}