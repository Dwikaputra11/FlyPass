package cthree.admin.flypass.utils

import com.auth0.android.jwt.JWT
import cthree.admin.flypass.models.admin.User

object Utils {

    fun decodeAccountToken(token: String): User {
        val user = JWT(token)
        return User(
            id = user.getClaim("id").asInt()!!,
            email = user.getClaim("email").asString()!!,
            accesstToken = user.getClaim("accesstToken").asString()!!,
        )
    }

}