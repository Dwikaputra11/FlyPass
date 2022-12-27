package cthree.user.flypass.models.flightpay.history


import com.google.gson.annotations.SerializedName

data class HistoryTopupList(
    @SerializedName("history")
    val history: List<History>
)