package com.example.finalproject.database.dao

import androidx.room.*
import com.example.finalproject.database.entity.StudentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(student: StudentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(students: List<StudentEntity>)

    @Query("SELECT * FROM students ORDER BY name ASC")
    fun getAllStudents(): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students ORDER BY nim ASC")
    fun getAllStudentsSortedByNim(): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students ORDER BY semester ASC")
    fun getAllStudentsSortedBySemester(): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students WHERE id = :id")
    suspend fun getStudentById(id: Int): StudentEntity?

    @Query("SELECT * FROM students WHERE name LIKE '%' || :keyword || '%' OR nim LIKE '%' || :keyword || '%' ORDER BY name ASC")
    fun searchStudents(keyword: String): Flow<List<StudentEntity>>

    @Update
    suspend fun update(student: StudentEntity)

    @Query("DELETE FROM students WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT COUNT(*) FROM students")
    suspend fun getStudentCount(): Int

    @Query("SELECT * FROM students ORDER BY name ASC")
    suspend fun getAllStudentsList(): List<StudentEntity>
}