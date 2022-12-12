package cthree.user.flypass.models.flight


import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class Flight(
    @SerializedName("Airline")
    val airline: Airline,
    @SerializedName("Airplane")
    val airplane: Airplane,
    @SerializedName("arrivalAirport")
    val arrivalAirport: ArrivalAirport,
    @SerializedName("arrivalDate")
    val arrivalDate: String,
    @SerializedName("arrivalTime")
    val arrivalTime: String,
    @SerializedName("baggage")
    val baggage: Int,
    @SerializedName("departureAirport")
    val departureAirport: DepartureAirport,
    @SerializedName("departureDate")
    val departureDate: String,
    @SerializedName("departureTime")
    val departureTime: String,
    @SerializedName("duration")
    val duration: String,
    @SerializedName("FlightClass")
    val flightClass: FlightClass,
    @SerializedName("flightCode")
    val flightCode: String,
    @SerializedName("FlightType")
    val flightType: FlightType,
    @SerializedName("id")
    val id: Int,
    @SerializedName("isAvailable")
    val isAvailable: Boolean,
    @SerializedName("price")
    val price: Int
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(Airline::class.java.classLoader)!!,
        parcel.readParcelable(Airplane::class.java.classLoader)!!,
        parcel.readParcelable(ArrivalAirport::class.java.classLoader)!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readParcelable(DepartureAirport::class.java.classLoader)!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readParcelable(FlightClass::class.java.classLoader)!!,
        parcel.readString()!!,
        parcel.readParcelable(FlightType::class.java.classLoader)!!,
        parcel.readInt(),
        parcel.readByte() != 0.toByte(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(airline, flags)
        parcel.writeParcelable(airplane, flags)
        parcel.writeParcelable(arrivalAirport, flags)
        parcel.writeString(arrivalDate)
        parcel.writeString(arrivalTime)
        parcel.writeInt(baggage)
        parcel.writeParcelable(departureAirport, flags)
        parcel.writeString(departureDate)
        parcel.writeString(departureTime)
        parcel.writeString(duration)
        parcel.writeParcelable(flightClass, flags)
        parcel.writeString(flightCode)
        parcel.writeParcelable(flightType, flags)
        parcel.writeInt(id)
        parcel.writeByte(if (isAvailable) 1 else 0)
        parcel.writeInt(price)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Flight> {
        override fun createFromParcel(parcel: Parcel): Flight {
            return Flight(parcel)
        }

        override fun newArray(size: Int): Array<Flight?> {
            return arrayOfNulls(size)
        }
    }
}