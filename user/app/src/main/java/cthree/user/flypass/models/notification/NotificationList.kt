package cthree.user.flypass.models.notification


import com.google.gson.annotations.SerializedName

data class NotificationList(
    @SerializedName("newNotification")
    val newNotification: Int,
    @SerializedName("notification")
    val notification: List<Notification>
)