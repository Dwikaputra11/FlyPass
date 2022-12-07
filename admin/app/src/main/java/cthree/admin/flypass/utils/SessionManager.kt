package cthree.admin.flypass.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(val context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("shared_pref", Context.MODE_PRIVATE)
    private var editor: SharedPreferences.Editor = prefs.edit()

    fun setToken(token: String){
        editor.putString("user_token", token)
        editor.apply()
    }

}