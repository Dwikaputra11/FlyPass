package cthree.user.flypass.utils

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import cthree.user.flypass.R
import java.text.ParseException
import java.text.SimpleDateFormat
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

    @SuppressLint("SimpleDateFormat")
    fun convertISOTime(context: Context, isoTime: String?, type: String): String {

        Log.d("Date", "convertISOTime: $isoTime")

        val sdf = SimpleDateFormat(context.getString(R.string.default_time_format), Locale.US)
        var convertedDate: Date? = null
        var formattedDate: String? = null
        var formattedTime = "10:00 AM"
        try {
            convertedDate = isoTime?.let { sdf.parse(it) }
            formattedDate = convertedDate?.let {
                SimpleDateFormat(context.getString(R.string.date_format)).format(
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

        if(Constants.TIME_TYPE == type) return formattedTime
        return "$formattedDate"
    }

    @SuppressLint("SimpleDateFormat")
    fun convertDateToDay(date:String): String{
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        val convertedDate: Date
        val formattedDate: String?
        try{
            convertedDate = date.let { sdf.parse(it) } as Date
            formattedDate = convertedDate.let {
                SimpleDateFormat("EEE, dd MMM").format(it)
            }
        }catch (e: Exception){
            e.printStackTrace()
            Log.e("Date", "convertDateToDay: ", )
            return ""
        }
        return "$formattedDate"
    }

    fun reverseDateFormat(cal:Calendar): String{
        val myFormat = "yyyy-MM-dd"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        return sdf.format(cal.time)
    }
}