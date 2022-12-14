package cthree.user.flypass.models.booking.bookings


import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class PassengerBooking(
    @SerializedName("bookingId")
    val bookingId: Int,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("passengerId")
    val passengerId: Int,
    @SerializedName("updatedAt")
    val updatedAt: String
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(bookingId)
        parcel.writeString(createdAt)
        parcel.writeInt(passengerId)
        parcel.writeString(updatedAt)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PassengerBooking> {
        override fun createFromParcel(parcel: Parcel): PassengerBooking {
            return PassengerBooking(parcel)
        }

        override fun newArray(size: Int): Array<PassengerBooking?> {
            return arrayOfNulls(size)
        }
    }
}