package cthree.admin.flypass.models.airline


import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "airlines")
data class Airline(
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("iata")
    val iata: String,
    @PrimaryKey
    @SerializedName("id")
    val id: Int,
    @SerializedName("image")
    val image: String,
    @SerializedName("imageId")
    val imageId: String?,
    @SerializedName("name")
    val name: String,
    @SerializedName("updatedAt")
    val updatedAt: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString(),
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(p0: Parcel?, p1: Int) {
        TODO("Not yet implemented")
    }

    companion object CREATOR : Parcelable.Creator<Airline> {
        override fun createFromParcel(parcel: Parcel): Airline {
            return Airline(parcel)
        }

        override fun newArray(size: Int): Array<Airline?> {
            return arrayOfNulls(size)
        }
    }
}