package com.futuradev.githubber.data.local

import android.content.Context
import android.content.SharedPreferences

class DataPersistence(private val context: Context) {

    private val sharedPref : SharedPreferences

    init {
        sharedPref = context.getSharedPreferences(GIT_HUBBER_PREFS, Context.MODE_PRIVATE)
    }

    var userToken : String
        get() = sharedPref.getString(USER_TOKEN, "") ?: ""
        set(value) = sharedPref.edit()
            .putString(USER_TOKEN, value)
            .apply()

    var deviceCode : String
        get() = sharedPref.getString(DEVICE_CODE, "") ?: ""
        set(value) = sharedPref.edit()
            .putString(DEVICE_CODE, value)
            .apply()

    var userCode : String
        get() = sharedPref.getString(USER_CODE, "") ?: ""
        set(value) = sharedPref.edit()
            .putString(USER_CODE, value)
            .apply()

    companion object {
        const val GIT_HUBBER_PREFS = "git_hubber_prefs"

        const val USER_TOKEN = "user_token"
        const val USER_CODE = "user_code"
        const val DEVICE_CODE = "device_code"
    }
}