package cthree.user.flypass.utils

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import com.auth0.android.jwt.JWT
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import cthree.user.flypass.R
import cthree.user.flypass.databinding.DialogTokenExpiredBinding
import cthree.user.flypass.databinding.DialogTwoButtonAlertBinding
import cthree.user.flypass.models.user.Profile
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.ParseException
import java.text.SimpleDateFormat
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

    @SuppressLint("SimpleDateFormat")
    fun convertISOTime(context: Context, isoTime: String?, type: String): String {

        Log.d("Date", "convertISOTime: $isoTime")

        val sdf = SimpleDateFormat(context.getString(R.string.default_time_format), Locale.US)
        val convertedDate: Date?
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

    fun covertYearMonDay(time: Date): String{
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val formattedDate: String?
        return try {
            formattedDate = sdf.format(time)
            formattedDate
        }catch (e: Exception){
            e.printStackTrace()
            Log.e("Date", "convertDateToDay: ")
            ""
        }
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
            Log.e("Date", "convertDateToDay: ")
            return ""
        }
        return "$formattedDate"
    }

    fun convertDateSearch(date:String): String{
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val convertedDate: Date
        val formattedDate: String?
        try{
            convertedDate = date.let { sdf.parse(it) } as Date
            formattedDate = convertedDate.let {
                SimpleDateFormat("EEE, dd MMM", Locale.US).format(it)
            }
        }catch (e: Exception){
            e.printStackTrace()
            Log.e("Date", "convertDateToDay: ")
            return ""
        }
        return "$formattedDate"
    }



    fun reverseDateFormat(cal:Calendar): String{
        val myFormat = "yyyy-MM-dd"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        return sdf.format(cal.time)
    }

    fun convertDateToDayDate(date: String): String{
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val convertedDate: Date
        val formattedDate: String?
        try{
            convertedDate = date.let { sdf.parse(it) } as Date
            formattedDate = convertedDate.let {
                SimpleDateFormat("E, dd MMM yyyy", Locale.US).format(it)
            }
        }catch (e: Exception){
            e.printStackTrace()
            Log.e("Date", "convertDateToDay: ")
            return ""
        }
        return "$formattedDate"
    }

    fun formattedTime(time: String): String{
        return time.subSequence(0..4).toString()
    }

    fun formattedDateOnly(date: String): String{
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val convertedDate: Date
        val formattedDate: String?
        try{
            convertedDate = date.let { sdf.parse(it) } as Date
            formattedDate = convertedDate.let {
                SimpleDateFormat("dd MMM yyyy", Locale.US).format(it)
            }
        }catch (e: Exception){
            e.printStackTrace()
            Log.e("Date", "convertDateToDay: ")
            return ""
        }
        return "$formattedDate"
    }

    fun formattedMoney(money: Int): String{
        var formattedString: String? = null
        try {
            val unusualSymbols = DecimalFormatSymbols()
            unusualSymbols.decimalSeparator = ','
            unusualSymbols.groupingSeparator = '.'
            val doubleVal: Double = money.toDouble()

            val formatter = DecimalFormat("#,##0.##", unusualSymbols)
            formatter.groupingSize = 3
            formattedString = formatter.format(doubleVal)
        }catch (nfe: NumberFormatException) {
            nfe.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "$formattedString"
    }

    fun decodeAccountToken(token: String): Profile {
        val user = JWT(token)
        Log.d("User", "decodeAccountToken: ${user.claims}")
        return Profile(
            id = user.getClaim("id").asInt()!!,
            email = user.getClaim("email").asString()!!,
            birthDate = user.getClaim("birthDate").asString()!!,
            gender = user.getClaim("gender").asString()!!,
            image = user.getClaim("image").asString(),
            phone = user.getClaim("phone").asString()!!,
            roleId = user.getClaim("roleId").asInt()!!,
            name = user.getClaim("name").asString()!!
        )
    }

    fun isTokenExpired(token: String): Boolean{
        val user = JWT(token)
        val expire = user.expiresAt
        val calendar = Calendar.getInstance()
        val isExpired = calendar.time.after(expire)
        Log.d("Token", "expired at: $expire")
        Log.d("Token", "isTokenExpired: $isExpired")
        return isExpired
    }

//    fun expiredTokenDialog(layoutInflater: LayoutInflater, context: Context): AuthOption{
//        val notEnoughBinding = DialogTokenExpiredBinding.inflate(layoutInflater, null, false)
//        val materialAlertDialogBuilder = MaterialAlertDialogBuilder(context)
//
//        materialAlertDialogBuilder.setView(notEnoughBinding.root)
//
//        val materAlertDialog = materialAlertDialogBuilder.create()
//        materAlertDialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
//        materAlertDialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
//        materAlertDialog.show()
//
//        notEnoughBinding.btnLogin.setOnClickListener {
//            Log.d("Alert Dialog", "expiredTokenDialog: ")
//            materAlertDialog.dismiss()
//        }
//        notEnoughBinding.btnRegister.setOnClickListener {
//            Log.d("Alert Dialog", "expiredTokenDialog: ")
//            materAlertDialog.dismiss()
//        }
//        notEnoughBinding.btnNanti.setOnClickListener {
//            Log.d("Alert Dialog", "expiredTokenDialog: ")
//            materAlertDialog.dismiss()
//        }
//    }
}