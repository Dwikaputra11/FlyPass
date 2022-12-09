package cthree.user.flypass.models.wishlist.get


import com.google.gson.annotations.SerializedName

data class WishList(
    @SerializedName("whistlist")
    val wishlistItem: List<WishListItem>
)