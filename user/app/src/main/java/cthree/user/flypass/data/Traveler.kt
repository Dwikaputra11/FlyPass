package cthree.user.flypass.data

import android.os.Parcel
import android.os.Parcelable

data class Traveler(
    val title: String,
    val name: String,
    val surname: String,
    val dateBirth:String,
    val idCard: String
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(name)
        parcel.writeString(surname)
        parcel.writeString(dateBirth)
        parcel.writeString(idCard)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Traveler> {
        override fun createFromParcel(parcel: Parcel): Traveler {
            return Traveler(parcel)
        }

        override fun newArray(size: Int): Array<Traveler?> {
            return arrayOfNulls(size)
        }
    }
}
