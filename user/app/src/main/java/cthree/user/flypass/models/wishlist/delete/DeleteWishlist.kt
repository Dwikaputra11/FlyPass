package cthree.user.flypass.models.wishlist.delete


import com.google.gson.annotations.SerializedName

data class DeleteWishlist(
    @SerializedName("del")
    val del: Int
)