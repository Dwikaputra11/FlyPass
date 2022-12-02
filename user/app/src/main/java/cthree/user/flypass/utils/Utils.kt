package cthree.user.flypass.utils

import java.util.*

object Utils {

    fun getCountryCode(countryName: String): String {
        val isoCountryCodes: Array<String> = Locale.getISOCountries()
        for (code in isoCountryCodes) {
            val locale = Locale("", code)
            if (countryName.equals(locale.displayCountry, ignoreCase = true)) {
                return code
            }
        }
        return ""
    }
}