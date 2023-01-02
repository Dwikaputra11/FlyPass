package cthree.admin.flypass.models.transaction


import com.google.gson.annotations.SerializedName

data class Transaction(
    @SerializedName("bookingId")
    val bookingId: Int,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("datePayed")
    val datePayed: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("Image")
    val image: String,
    @SerializedName("ImageId")
    val imageId: String,
    @SerializedName("isPayed")
    val isPayed: Boolean,
    @SerializedName("updatedAt")
    val updatedAt: String
)