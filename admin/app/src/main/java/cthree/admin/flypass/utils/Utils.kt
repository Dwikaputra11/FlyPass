package cthree.admin.flypass.utils

import com.auth0.android.jwt.JWT
import cthree.admin.flypass.models.admin.UserAdmin
import java.util.*

object Utils {

    fun getCountryCode(countryName: String): String? {
        val isoCountryCodes = Locale.getISOCountries()
        for (code in isoCountryCodes) {
            val locale = Locale("", code)
            if (countryName.equals(locale.displayCountry, ignoreCase = true)) {
                return code
            }
        }
        return null
    }

    fun decodeAccountToken(token: String): UserAdmin {
        val user = JWT(token)
        return UserAdmin(
            id = user.getClaim("id").asInt()!!,
            email = user.getClaim("email").asString()!!,
            accesstToken = user.getClaim("accesstToken").asString()!!,
        )
    }

}