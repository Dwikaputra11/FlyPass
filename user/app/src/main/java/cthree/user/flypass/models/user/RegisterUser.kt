package cthree.user.flypass.models.user

data class RegisterUser(
    val email: String,
    val password: String,
    val confPassword: String
)
