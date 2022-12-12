package cthree.user.flypass.data

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recent_search")
data class RecentSearch(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val departCity: String,
    val arriveCity: String,
    val iataDepartAirport: String,
    val iataArriveAirport : String,
    val departDate: String,
    val arriveDate: String?,
    val passengerAmount: Int,
    val seatClass: String,
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString(),
        parcel.readInt(),
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(departCity)
        parcel.writeString(arriveCity)
        parcel.writeString(iataDepartAirport)
        parcel.writeString(iataArriveAirport)
        parcel.writeString(departDate)
        parcel.writeString(arriveDate)
        parcel.writeInt(passengerAmount)
        parcel.writeString(seatClass)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RecentSearch> {
        override fun createFromParcel(parcel: Parcel): RecentSearch {
            return RecentSearch(parcel)
        }

        override fun newArray(size: Int): Array<RecentSearch?> {
            return arrayOfNulls(size)
        }
    }
}
