package cthree.user.flypass.models.user

data class RegisterUser(
    val email: String,
    val password: String,
    val confirmationPassword: String,
    val name: String,
    val birthDate: String,
    val gender: String,
    val phone: String
)
