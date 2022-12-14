package cthree.admin.flypass.models.admin

data class RegisterAdminDataClass(
    val name: String,
    val email: String,
    val password: String,
    val confirmationPassword: String,
    val birthDate: String
)
