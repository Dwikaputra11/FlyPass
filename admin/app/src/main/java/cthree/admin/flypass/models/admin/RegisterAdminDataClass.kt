package cthree.admin.flypass.models.admin

data class RegisterAdminDataClass(
    val name: String,
    val email: String,
    val password: String,
    val confirmPassword: String,
    val birthDate: String
)
