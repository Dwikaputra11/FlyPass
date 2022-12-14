package cthree.user.flypass.models.booking.bookings


import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class BookingStatus(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BookingStatus> {
        override fun createFromParcel(parcel: Parcel): BookingStatus {
            return BookingStatus(parcel)
        }

        override fun newArray(size: Int): Array<BookingStatus?> {
            return arrayOfNulls(size)
        }
    }
}