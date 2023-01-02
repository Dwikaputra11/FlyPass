package cthree.admin.flypass.utils

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.auth0.android.jwt.JWT
import cthree.admin.flypass.R
import cthree.admin.flypass.models.admin.UserAdmin
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object Utils {

    @SuppressLint("SimpleDateFormat")
    fun convertISOTime(context: Context, isoTime: String?): String {

        Log.d("Date", "convertISOTime: $isoTime")

        val sdf = SimpleDateFormat(context.getString(R.string.default_time_format), Locale.US)
        val convertedDate: Date?
        var formattedDate: String? = null
        var formattedTime = "10:00 AM"
        try {
            convertedDate = isoTime?.let { sdf.parse(it) }
            formattedDate = convertedDate?.let {
                SimpleDateFormat("E, dd MMM yyy").format(
                    it
                )
            }
            formattedTime = convertedDate?.let {
                SimpleDateFormat(context.getString(R.string.time_format)).format(
                    it
                )
            }.toString()

//            Log.e("Time", formattedTime.toString())

            if((formattedTime.subSequence(6,8).toString() == "PM" || formattedTime.subSequence(6,8).toString() == "pm") && formattedTime.subSequence(0,2).toString().toInt()>12){
                formattedTime = (formattedTime.subSequence(0,2).toString().toInt()-12).toString()+formattedTime.subSequence(2,8).toString()
            }
            if (formattedTime.subSequence(0,2).toString() == "00"){
                formattedTime = (formattedTime.subSequence(0,2).toString().toInt()+1).toString()+formattedTime.subSequence(2,8).toString()

            }
            if (formattedTime.subSequence(0,2).toString() == "0:"){
                formattedTime = (formattedTime.subSequence(0,1).toString().toInt()+1).toString()+formattedTime.subSequence(2,8).toString()

            }


//            Log.d("Date ", "$formattedDate | $formattedTime")
        } catch (e: ParseException) {
            e.printStackTrace()
            Log.d("Date", "convertISOTime: $formattedDate")
            Log.e("Error Date ", e.message!!)
        }

        return "$formattedDate"
    }

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