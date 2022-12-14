package cthree.user.flypass.models.booking.bookings


import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class Passenger(
    @SerializedName("age")
    val age: Int?,
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
    @SerializedName("PassengerBooking")
    val passengerBooking: PassengerBooking,
    @SerializedName("updatedAt")
    val updatedAt: String
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readParcelable(PassengerBooking::class.java.classLoader)!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(age)
        parcel.writeString(createdAt)
        parcel.writeString(firstName)
        parcel.writeInt(id)
        parcel.writeString(identityNumber)
        parcel.writeString(identityType)
        parcel.writeString(lastName)
        parcel.writeParcelable(passengerBooking, flags)
        parcel.writeString(updatedAt)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Passenger> {
        override fun createFromParcel(parcel: Parcel): Passenger {
            return Passenger(parcel)
        }

        override fun newArray(size: Int): Array<Passenger?> {
            return arrayOfNulls(size)
        }
    }
}