package cthree.user.flypass.data

data class RegisterGoogle(
    val IdToken: String,
    val name: String,
    val birthDate: String,
    val gender: String,
    val phone: String
)
