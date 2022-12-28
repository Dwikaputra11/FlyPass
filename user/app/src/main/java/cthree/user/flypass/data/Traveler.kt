package cthree.user.flypass.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Traveler(
    val title: String,
    val firstName: String,
    val lastName: String,
    val dateBirth:String,
    val idCard: String
):Parcelable
