package cthree.user.flypass.utils

import android.content.Context
import android.content.SharedPreferences
import cthree.user.flypass.models.airport.Airport

class SessionManager(
    val context: Context
) {

    private val prefs: SharedPreferences = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE)
    private var editor: SharedPreferences.Editor = prefs.edit()

    fun setPassengerAmount(amount: Int){
        editor.putInt(Constants.PASSENGER_AMOUNT, amount)
        editor.apply()
    }
    fun getPassenger(): Int = prefs.getInt(Constants.PASSENGER_AMOUNT, 0)

    fun setSeatClass(seatClass: String){
        editor.putString(Constants.SEAT_CLASS, seatClass)
        editor.apply()
    }
    fun getSeatClass(): String? = prefs.getString(Constants.SEAT_CLASS, "")

    fun setIsFirstInstall(set: Boolean){
        editor.putBoolean(Constants.IS_FIRST_INSTALL, set)
        editor.apply()
    }

    fun getToken(): String? = prefs.getString(Constants.USER_TOKEN, null)

    fun setToken(token: String){
        editor.putString(Constants.USER_TOKEN, token)
        editor.apply()
    }

    fun setUserId(id: Int){
        editor.putInt(Constants.USER_ID, id)
        editor.apply()
    }

    fun getUserId(): Int = prefs.getInt(Constants.USER_ID,-1)

    fun getIsFirstInstall(): Boolean = prefs.getBoolean(Constants.IS_FIRST_INSTALL, true)


    fun setIsAirportDBExist(){
        editor.putBoolean(Constants.IS_AIRPORT_DB_EXIST, true)
        editor.apply()
    }

    fun setSelectAirport(airport: Airport, type: String){
        if(type == Constants.DEPART_AIRPORT){
            editor.putString(Constants.DEPART_AIRPORT_CITY, airport.city)
            editor.putInt(Constants.DEPART_AIRPORT_ID, airport.id)
            editor.putString(Constants.DEPART_AIRPORT_IATA, airport.iata)
            editor.putString(Constants.DEPART_AIRPORT_COUNTRY, airport.country)
            editor.putString(Constants.DEPART_AIRPORT_NAME, airport.name)
        } else {
            editor.putString(Constants.ARRIVE_AIRPORT_CITY, airport.city)
            editor.putInt(Constants.ARRIVE_AIRPORT_ID, airport.id)
            editor.putString(Constants.ARRIVE_AIRPORT_IATA, airport.iata)
            editor.putString(Constants.ARRIVE_AIRPORT_COUNTRY, airport.country)
            editor.putString(Constants.ARRIVE_AIRPORT_NAME, airport.name)
        }
        editor.apply()
    }

    fun getSelectedAirport(type: String): Airport {
        return if(type == Constants.DEPART_AIRPORT){
            val city = prefs.getString(Constants.DEPART_AIRPORT_CITY, Constants.DEPART_DEFAULT_VAL)
            val country = prefs.getString(Constants.DEPART_AIRPORT_COUNTRY, Constants.DEPART_DEFAULT_VAL)
            val iata = prefs.getString(Constants.DEPART_AIRPORT_IATA, Constants.DEPART_DEFAULT_VAL)
            val id = prefs.getInt(Constants.DEPART_AIRPORT_ID, 0)
            val name = prefs.getString(Constants.DEPART_AIRPORT_NAME, Constants.DEPART_DEFAULT_VAL)
            Airport(city!!, country!!, iata!!, id, name!!)
        }else{
            val city = prefs.getString(Constants.ARRIVE_AIRPORT_CITY, Constants.ARRIVE_DEFAULT_VAL)
            val country = prefs.getString(Constants.ARRIVE_AIRPORT_COUNTRY, Constants.ARRIVE_DEFAULT_VAL)
            val iata = prefs.getString(Constants.ARRIVE_AIRPORT_IATA, Constants.ARRIVE_DEFAULT_VAL)
            val id = prefs.getInt(Constants.ARRIVE_AIRPORT_ID, 0)
            val name = prefs.getString(Constants.ARRIVE_AIRPORT_NAME, Constants.ARRIVE_DEFAULT_VAL)
            Airport(city!!, country!!, iata!!, id, name!!)
        }
    }

    fun clearAirport(){
        editor.remove(Constants.DEPART_AIRPORT_CITY)
        editor.remove(Constants.DEPART_AIRPORT_ID)
        editor.remove(Constants.DEPART_AIRPORT_IATA)
        editor.remove(Constants.DEPART_AIRPORT_COUNTRY)
        editor.remove(Constants.DEPART_AIRPORT_NAME)

        editor.remove(Constants.ARRIVE_AIRPORT_CITY)
        editor.remove(Constants.ARRIVE_AIRPORT_ID)
        editor.remove(Constants.ARRIVE_AIRPORT_IATA)
        editor.remove(Constants.ARRIVE_AIRPORT_COUNTRY)
        editor.remove(Constants.ARRIVE_AIRPORT_NAME)

        editor.apply()
    }

}