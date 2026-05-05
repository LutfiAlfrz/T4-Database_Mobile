package com.example.finalproject.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalproject.database.AppDatabase
import com.example.finalproject.database.entity.StudentEntity
import com.example.finalproject.preferences.AppPreferences
import com.example.finalproject.preferences.SessionManager
import com.example.finalproject.storage.ActivityLogger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)
    private val dao = db.studentDao()
    val prefs = AppPreferences(application)
    private val session = SessionManager(application)
    val logger = ActivityLogger(application)

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage

    fun getStudents(): Flow<List<StudentEntity>> {
        return when (prefs.sortOrder) {
            AppPreferences.SORT_BY_NIM -> dao.getAllStudentsSortedByNim()
            AppPreferences.SORT_BY_SEMESTER -> dao.getAllStudentsSortedBySemester()
            else -> dao.getAllStudents()
        }
    }

    fun deleteStudent(id: Int, name: String) {
        viewModelScope.launch {
            dao.deleteById(id)
            logger.log(ActivityLogger.ACTION_DELETE, "Mahasiswa \"$name\" dihapus")
            _snackbarMessage.value = "Data berhasil dihapus"
        }
    }

    fun saveSortOrder(sortOrder: String) {
        prefs.sortOrder = sortOrder
    }

    fun logout() {
        logger.log(ActivityLogger.ACTION_LOGOUT, "User \"${session.userUsername}\" logout")
        session.clearSession()
    }

    fun clearSnackbar() { _snackbarMessage.value = null }
}