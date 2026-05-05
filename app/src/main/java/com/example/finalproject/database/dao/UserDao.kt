package com.example.finalproject.database.dao

import androidx.room.*
import com.example.finalproject.database.entity.UserEntity

@Dao
interface UserDao {
    // Insert data registrasi
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(user: UserEntity)

    // Validasi login berdasarkan username + password
    @Query("SELECT * FROM users WHERE username = :username AND password = :password LIMIT 1")
    suspend fun login(username: String, password: String): UserEntity?

    // Pengecekan apakah username sudah dipakai
    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun findByUsername(username: String): UserEntity?

    // Pengecekan apakah email sudah dipakai
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun findByEmail(email: String): UserEntity?
}