package cthree.user.flypass.models.wishlist.post


import com.google.gson.annotations.SerializedName

data class WishlistResponse(
    @SerializedName("whistlist")
    val wishlist: Wishlist
)