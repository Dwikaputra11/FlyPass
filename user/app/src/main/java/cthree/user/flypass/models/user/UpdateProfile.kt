package cthree.user.flypass.models.user


data class UpdateProfile(
    val email: String,
    val image: String,
    val name: String,
    val phone: String,
    val roleId: Int
)
