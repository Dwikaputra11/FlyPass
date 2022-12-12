package cthree.user.flypass.models.flight


import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class Airplane(
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("icao")
    val icao: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("model")
    val model: String,
    @SerializedName("updatedAt")
    val updatedAt: String
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(createdAt)
        parcel.writeString(icao)
        parcel.writeInt(id)
        parcel.writeString(model)
        parcel.writeString(updatedAt)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Airplane> {
        override fun createFromParcel(parcel: Parcel): Airplane {
            return Airplane(parcel)
        }

        override fun newArray(size: Int): Array<Airplane?> {
            return arrayOfNulls(size)
        }
    }
}