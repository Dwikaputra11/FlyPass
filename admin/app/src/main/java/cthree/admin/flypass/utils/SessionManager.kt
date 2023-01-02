package cthree.admin.flypass.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(val context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("shared_pref", Context.MODE_PRIVATE)
    private var editor: SharedPreferences.Editor = prefs.edit()

    fun getIsFirstInstall(): Boolean = prefs.getBoolean(Constants.IS_FIRST_INSTALL, true)

    fun setIsFirstInstall(set: Boolean){
        editor.putBoolean(Constants.IS_FIRST_INSTALL, set)
        editor.apply()
    }

    fun setToken(token: String){
        editor.putString("user_token", token)
        editor.apply()
    }

    fun getToken() : String?{
        return prefs.getString("user_token", null)
    }

//    fun saveDetailTicket()

}