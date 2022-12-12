package cthree.admin.flypass.utils

import com.auth0.android.jwt.JWT
import cthree.admin.flypass.models.admin.UserAdmin

object Utils {

    fun decodeAccountToken(token: String): UserAdmin {
        val user = JWT(token)
        return UserAdmin(
            id = user.getClaim("id").asInt()!!,
            email = user.getClaim("email").asString()!!,
            accesstToken = user.getClaim("accesstToken").asString()!!,
        )
    }

}