package cthree.user.flypass.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.auth0.android.jwt.JWT
import cthree.user.flypass.R
import cthree.user.flypass.data.Traveler
import cthree.user.flypass.models.booking.bookings.FlightBooking
import cthree.user.flypass.models.booking.bookings.Passenger
import cthree.user.flypass.models.flight.Airline
import cthree.user.flypass.models.flight.Airplane
import cthree.user.flypass.models.flight.Flight
import cthree.user.flypass.models.login.LoginData
import cthree.user.flypass.models.user.Profile
import cthree.user.flypass.ui.dialog.DialogCaller
import cthree.user.flypass.utils.Constants.CHANNEL_ID
import cthree.user.flypass.utils.Constants.NOTIFICATION_ID
import cthree.user.flypass.utils.Constants.NOTIFICATION_TITLE
import cthree.user.flypass.utils.Constants.VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION
import cthree.user.flypass.utils.Constants.VERBOSE_NOTIFICATION_CHANNEL_NAME
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
    fun convertISOTime(context: Context, isoTime: String?): String {

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

    fun convertDateToDayMonYear(date:String): String{
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val convertedDate: Date
        val formattedDate: String?
        try{
            convertedDate = date.let { sdf.parse(it) } as Date
            formattedDate = convertedDate.let {
                SimpleDateFormat("EEE, dd MMM yyyy", Locale.US).format(it)
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

    fun formattedDuration(time: String): String{
        val raw = time.subSequence(0..4).toString()
        val splitTime = raw.split(":")
        val hour = splitTime[0].toInt()
        val minute = splitTime[1].toInt()
        return "${hour}h ${minute}m"
    }

//    fun decodeAccountToken(token: String): Profile? {
//        val user = JWT(token)
//        Log.d("User", "decodeAccountToken: ${user.claims}")
//        val claim = user.getClaim("user").asObject(UserData::class.java)
//        Log.d("Claim", "decodeAccountToken: $claim")
//        if (claim != null) {
//            return Profile(
//                id = claim.id,
//                email = claim.email,
//                birthDate = claim.birthDate,
//                gender = claim.gender,
//                image = claim.image,
//                phone = claim.phone,
//                roleId = claim.roleId,
//                name = claim.name
//            )
//        }
//        return null
//    }

    fun decodeAccountToken(token: String): Profile {
        val user = JWT(token)
        Log.d("User", "decodeAccountToken: ${user.claims}")
        return Profile(
            id = user.getClaim("id").asInt()!!,
            email = user.getClaim("email").asString()!!,
            birthDate = user.getClaim("birthDate").asString(),
            gender = user.getClaim("gender").asString(),
            image = user.getClaim("image").asString(),
            phone = user.getClaim("phone").asString(),
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

    fun getDateNow(): String{
        val cal = Calendar.getInstance()
        val formattedDate: String?
        try{
            formattedDate = cal.time.let {
                SimpleDateFormat("E, dd MMM yyyy", Locale.US).format(it)
            }
        }catch (e: Exception){
            e.printStackTrace()
            Log.e("Date", "convertDateToDay: ")
            return ""
        }
        return "$formattedDate"

    }

    fun makeStatusNotification(message: String, context: Context) {

        // Make a channel if necessary
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            val name = VERBOSE_NOTIFICATION_CHANNEL_NAME
            val description = VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.description = description

            // Add the channel
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

            notificationManager?.createNotificationChannel(channel)
        }

        // Create the notification
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(NOTIFICATION_TITLE)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVibrate(LongArray(0))

        // Show the notification
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, builder.build())
    }

    fun passengerToTravelerDataClass(passenger: List<Passenger>): List<Traveler?>{
        val travelers = passenger.map {
            Traveler(
                title = "Mr",
                firstName = it.firstName,
                lastName = it.lastName,
                dateBirth = "2001-05-20",
                idCard = it.identityNumber
            )
        }.toList()
        return travelers
    }
}