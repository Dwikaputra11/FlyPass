package cthree.user.flypass.models.notification


import com.google.gson.annotations.SerializedName

data class Notification(
    @SerializedName("admin")
    val admin: Boolean,
    @SerializedName("bookingCode")
    val bookingCode: String,
    @SerializedName("bookingId")
    val bookingId: Int,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("isRead")
    val isRead: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("updatedAt")
    val updatedAt: String,
    @SerializedName("userId")
    val userId: Int
)