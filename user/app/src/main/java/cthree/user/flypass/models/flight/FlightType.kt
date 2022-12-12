package cthree.user.flypass.models.flight


import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class FlightType(
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

    companion object CREATOR : Parcelable.Creator<FlightType> {
        override fun createFromParcel(parcel: Parcel): FlightType {
            return FlightType(parcel)
        }

        override fun newArray(size: Int): Array<FlightType?> {
            return arrayOfNulls(size)
        }
    }
}