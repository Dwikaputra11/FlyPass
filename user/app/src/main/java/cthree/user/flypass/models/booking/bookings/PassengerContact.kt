package cthree.user.flypass.models.booking.bookings


import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class PassengerContact(
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("firstName")
    val firstName: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("lastName")
    val lastName: String,
    @SerializedName("phone")
    val phone: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("updatedAt")
    val updatedAt: String
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(createdAt)
        parcel.writeString(email)
        parcel.writeString(firstName)
        parcel.writeInt(id)
        parcel.writeString(lastName)
        parcel.writeString(phone)
        parcel.writeString(title)
        parcel.writeString(updatedAt)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PassengerContact> {
        override fun createFromParcel(parcel: Parcel): PassengerContact {
            return PassengerContact(parcel)
        }

        override fun newArray(size: Int): Array<PassengerContact?> {
            return arrayOfNulls(size)
        }
    }
}