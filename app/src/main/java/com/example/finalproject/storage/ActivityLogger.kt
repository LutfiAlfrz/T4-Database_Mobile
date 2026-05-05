package com.example.finalproject.storage

import android.content.Context
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ActivityLogger(private val context: Context) {
    companion object {
        private const val LOG_FILENAME = "activity_log.txt"
        const val ACTION_ADD = "TAMBAH"
        const val ACTION_EDIT = "EDIT"
        const val ACTION_DELETE = "HAPUS"
        const val ACTION_LOGIN = "LOGIN"
        const val ACTION_LOGOUT = "LOGOUT"
        const val ACTION_REGISTER = "DAFTAR"
    }

    private val logFile: File
        get() = File(context.filesDir, LOG_FILENAME)

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale("id"))

    fun log(action: String, detail: String) {
        val timestamp = dateFormat.format(Date())
        val logLine = "[$timestamp] $action: $detail\n"
        try {
            FileWriter(logFile, true).use { it.write(logLine) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun readLog(): String {
        return if (logFile.exists()) logFile.readText() else "Belum ada aktivitas."
    }

    fun readLogLines(): List<String> {
        return if (logFile.exists()) logFile.readLines().filter { it.isNotBlank() }.reversed()
        else emptyList()
    }

    fun clearLog() {
        if (logFile.exists()) logFile.delete()
    }

    fun isLogAvailable(): Boolean = logFile.exists() && logFile.length() > 0
}