package cthree.user.flypass.models.flightpay


import com.google.gson.annotations.SerializedName

data class UserWallet(
    @SerializedName("wallet")
    val wallet: Wallet
)