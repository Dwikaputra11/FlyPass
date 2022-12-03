package cthree.user.flypass.models.flight


import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class FlightClass(
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("updatedAt")
    val updatedAt: String
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(createdAt)
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(updatedAt)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FlightClass> {
        override fun createFromParcel(parcel: Parcel): FlightClass {
            return FlightClass(parcel)
        }

        override fun newArray(size: Int): Array<FlightClass?> {
            return arrayOfNulls(size)
        }
    }
}