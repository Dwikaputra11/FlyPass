package cthree.admin.flypass.models.user


import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("birthDate")
    val birthDate: String?,
    @SerializedName("email")
    val email: String,
    @SerializedName("gender")
    val gender: String?,
    @SerializedName("id")
    val id: Int,
    @SerializedName("image")
    val image: String?,
    @SerializedName("name")
    val name: String,
    @SerializedName("phone")
    val phone: String?,
    @SerializedName("Role")
    val role: Role
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString()!!,
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString()!!,
        parcel.readString(),
        parcel.readParcelable(Role::class.java.classLoader)!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(birthDate)
        parcel.writeString(email)
        parcel.writeString(gender)
        parcel.writeInt(id)
        parcel.writeString(image)
        parcel.writeString(name)
        parcel.writeString(phone)
        parcel.writeParcelable(role, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}