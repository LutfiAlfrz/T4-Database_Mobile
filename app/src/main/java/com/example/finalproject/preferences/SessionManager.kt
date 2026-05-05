package com.example.finalproject.preferences

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "user_session"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_NAMA = "user_nama"
        private const val KEY_USER_USERNAME = "user_username"
        private const val KEY_USER_EMAIL = "user_email"
    }

    fun saveSession(id: Int, nama: String, username: String, email: String) {
        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .putInt(KEY_USER_ID, id)
            .putString(KEY_USER_NAMA, nama)
            .putString(KEY_USER_USERNAME, username)
            .putString(KEY_USER_EMAIL, email)
            .apply()
    }

    val isLoggedIn: Boolean
        get() = prefs.getBoolean(KEY_IS_LOGGED_IN, false)

    val userId: Int get() = prefs.getInt(KEY_USER_ID, -1)
    val userNama: String get() = prefs.getString(KEY_USER_NAMA, "") ?: ""
    val userUsername: String get() = prefs.getString(KEY_USER_USERNAME, "") ?: ""
    val userEmail: String get() = prefs.getString(KEY_USER_EMAIL, "") ?: ""

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}