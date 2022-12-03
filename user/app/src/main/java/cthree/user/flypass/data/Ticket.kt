package cthree.user.flypass.data

import android.os.Parcel
import android.os.Parcelable

data class Ticket (
//    val departureTime: String,
//    val arrivalTime: String,
    val iataDeparture: String,
    val iataArrival: String?,
    val duration: Int,
//    val airplaneName: String,
    val price: Int,
    val seatClass: String,
//    val flightCode: String
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(iataDeparture)
        parcel.writeString(iataArrival)
        parcel.writeInt(duration)
        parcel.writeInt(price)
        parcel.writeString(seatClass)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Ticket> {
        override fun createFromParcel(parcel: Parcel): Ticket {
            return Ticket(parcel)
        }

        override fun newArray(size: Int): Array<Ticket?> {
            return arrayOfNulls(size)
        }
    }
}