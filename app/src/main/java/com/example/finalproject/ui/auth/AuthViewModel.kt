package com.example.finalproject.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalproject.database.AppDatabase
import com.example.finalproject.database.entity.UserEntity
import com.example.finalproject.preferences.SessionManager
import com.example.finalproject.storage.ActivityLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthResult {
    object Idle : AuthResult()
    object Loading : AuthResult()
    object Success : AuthResult()
    data class Error(val message: String) : AuthResult()
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val userDao = AppDatabase.getInstance(application).userDao()
    val session = SessionManager(application)
    private val logger = ActivityLogger(application)

    private val _loginResult = MutableStateFlow<AuthResult>(AuthResult.Idle)
    val loginResult: StateFlow<AuthResult> = _loginResult

    private val _registerResult = MutableStateFlow<AuthResult>(AuthResult.Idle)
    val registerResult: StateFlow<AuthResult> = _registerResult

    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _loginResult.value = AuthResult.Error("Username dan password tidak boleh kosong")
            return
        }
        viewModelScope.launch {
            _loginResult.value = AuthResult.Loading
            val user = userDao.login(username.trim(), password.trim())
            if (user != null) {
                session.saveSession(user.id, user.nama, user.username, user.email)
                logger.log(ActivityLogger.ACTION_LOGIN, "User \"${user.username}\" berhasil login")
                _loginResult.value = AuthResult.Success
            } else {
                logger.log(ActivityLogger.ACTION_LOGIN, "Gagal login dengan username \"$username\"")
                _loginResult.value = AuthResult.Error("Username atau password salah")
            }
        }
    }

    fun register(
        nama: String, username: String, email: String,
        password: String, konfirmasiPassword: String
    ) {
        when {
            nama.isBlank() -> { _registerResult.value = AuthResult.Error("Nama tidak boleh kosong"); return }
            username.isBlank() -> { _registerResult.value = AuthResult.Error("Username tidak boleh kosong"); return }
            username.length < 4 -> { _registerResult.value = AuthResult.Error("Username minimal 4 karakter"); return }
            email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                _registerResult.value = AuthResult.Error("Format email tidak valid"); return
            }
            password.isBlank() -> { _registerResult.value = AuthResult.Error("Password tidak boleh kosong"); return }
            password.length < 6 -> { _registerResult.value = AuthResult.Error("Password minimal 6 karakter"); return }
            password != konfirmasiPassword -> { _registerResult.value = AuthResult.Error("Konfirmasi password tidak cocok"); return }
        }

        viewModelScope.launch {
            _registerResult.value = AuthResult.Loading
            if (userDao.findByUsername(username.trim()) != null) {
                _registerResult.value = AuthResult.Error("Username sudah digunakan"); return@launch
            }
            if (userDao.findByEmail(email.trim()) != null) {
                _registerResult.value = AuthResult.Error("Email sudah digunakan"); return@launch
            }
            userDao.insert(UserEntity(nama = nama.trim(), username = username.trim(), email = email.trim(), password = password.trim()))
            logger.log(ActivityLogger.ACTION_REGISTER, "User baru \"$username\" berhasil mendaftar")
            _registerResult.value = AuthResult.Success
        }
    }

    fun resetLoginResult() { _loginResult.value = AuthResult.Idle }
    fun resetRegisterResult() { _registerResult.value = AuthResult.Idle }
}