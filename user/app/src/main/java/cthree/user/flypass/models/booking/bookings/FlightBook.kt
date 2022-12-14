package cthree.user.flypass.models.booking.bookings


import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class FlightBook(
    @SerializedName("airlineId")
    val airlineId: Int,
    @SerializedName("airplaneId")
    val airplaneId: Int,
    @SerializedName("arrivalAirportId")
    val arrivalAirportId: Int,
    @SerializedName("arrivalDate")
    val arrivalDate: String,
    @SerializedName("arrivalTime")
    val arrivalTime: String,
    @SerializedName("baggage")
    val baggage: Int,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("departureAirportId")
    val departureAirportId: Int,
    @SerializedName("departureDate")
    val departureDate: String,
    @SerializedName("departureTime")
    val departureTime: String,
    @SerializedName("duration")
    val duration: String,
    @SerializedName("flightClassId")
    val flightClassId: Int,
    @SerializedName("flightCode")
    val flightCode: String,
    @SerializedName("flightTypeId")
    val flightTypeId: Int,
    @SerializedName("id")
    val id: Int,
    @SerializedName("isAvailable")
    val isAvailable: Boolean,
    @SerializedName("price")
    val price: Int,
    @SerializedName("updatedAt")
    val updatedAt: String
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readInt(),
        parcel.readByte() != 0.toByte(),
        parcel.readInt(),
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(airlineId)
        parcel.writeInt(airplaneId)
        parcel.writeInt(arrivalAirportId)
        parcel.writeString(arrivalDate)
        parcel.writeString(arrivalTime)
        parcel.writeInt(baggage)
        parcel.writeString(createdAt)
        parcel.writeInt(departureAirportId)
        parcel.writeString(departureDate)
        parcel.writeString(departureTime)
        parcel.writeString(duration)
        parcel.writeInt(flightClassId)
        parcel.writeString(flightCode)
        parcel.writeInt(flightTypeId)
        parcel.writeInt(id)
        parcel.writeByte(if (isAvailable) 1 else 0)
        parcel.writeInt(price)
        parcel.writeString(updatedAt)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FlightBook> {
        override fun createFromParcel(parcel: Parcel): FlightBook {
            return FlightBook(parcel)
        }

        override fun newArray(size: Int): Array<FlightBook?> {
            return arrayOfNulls(size)
        }
    }
}