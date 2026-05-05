package com.example.finalproject.preferences

import android.content.Context
import android.content.SharedPreferences

class AppPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "student_directory_prefs"

        const val KEY_SORT_ORDER = "sort_order"
        const val KEY_LAST_SEARCH = "last_search"
        const val KEY_TOTAL_ADDED = "total_added"

        const val SORT_BY_NAME = "name"
        const val SORT_BY_NIM = "nim"
        const val SORT_BY_SEMESTER = "semester"
    }

    var sortOrder: String
        get() = prefs.getString(KEY_SORT_ORDER, SORT_BY_NAME) ?: SORT_BY_NAME
        set(value) = prefs.edit().putString(KEY_SORT_ORDER, value).apply()

    var lastSearch: String
        get() = prefs.getString(KEY_LAST_SEARCH, "") ?: ""
        set(value) = prefs.edit().putString(KEY_LAST_SEARCH, value).apply()

    var totalAdded: Int
        get() = prefs.getInt(KEY_TOTAL_ADDED, 0)
        set(value) = prefs.edit().putInt(KEY_TOTAL_ADDED, value).apply()

    fun incrementTotalAdded() {
        totalAdded += 1
    }

    fun clearAll() {
        prefs.edit().clear().apply()
    }
}