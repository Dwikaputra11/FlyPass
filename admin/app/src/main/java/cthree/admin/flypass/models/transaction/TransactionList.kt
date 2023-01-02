package cthree.admin.flypass.models.transaction


import com.google.gson.annotations.SerializedName

data class TransactionList(
    @SerializedName("transaction")
    val transaction: List<Transaction>
)