package cthree.user.flypass.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(
    val context: Context
) {

    private val prefs: SharedPreferences = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE)
    private var editor: SharedPreferences.Editor = prefs.edit()

    fun setPassengerAmount(amount: Int){
        editor.putInt(Constants.PASSENGER_AMOUNT, amount)
        editor.apply()
    }

    fun setIsFirstInstall(){
        editor.putBoolean(Constants.IS_FIRST_INSTALL, true)
        editor.apply()
    }

    fun getIsFirstInstall(): Boolean = prefs.getBoolean(Constants.IS_FIRST_INSTALL, false)

    fun getPassenger(): Int = prefs.getInt(Constants.PASSENGER_AMOUNT, 2)

}