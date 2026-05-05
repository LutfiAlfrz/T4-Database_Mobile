package com.example.finalproject.storage

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.finalproject.database.entity.StudentEntity
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class InternalStorageManager(private val context: Context) {

    companion object {
        const val BACKUP_FILENAME = "student_backup.txt"
        const val EXPORT_DIR = "exports"
    }

    fun exportToTxt(students: List<StudentEntity>): File {
        val exportDir = File(context.filesDir, EXPORT_DIR)
        if (!exportDir.exists()) exportDir.mkdirs()

        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val exportFile = File(exportDir, "mahasiswa_$timestamp.txt")

        FileWriter(exportFile).use { writer ->
            writer.appendLine("=" .repeat(50))
            writer.appendLine("       DATA MAHASISWA - STUDENT DIRECTORY")
            writer.appendLine("=" .repeat(50))
            writer.appendLine("Diekspor pada: ${SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())}")
            writer.appendLine("Total mahasiswa: ${students.size}")
            writer.appendLine("=" .repeat(50))
            writer.appendLine()

            students.forEachIndexed { index, student ->
                writer.appendLine("No. ${index + 1}")
                writer.appendLine("Nama     : ${student.name}")
                writer.appendLine("NIM      : ${student.nim}")
                writer.appendLine("Prodi    : ${student.prodi}")
                writer.appendLine("Email    : ${student.email}")
                writer.appendLine("Semester : ${student.semester}")
                writer.appendLine("-".repeat(30))
            }
        }
        return exportFile
    }

    fun exportToCsv(students: List<StudentEntity>): File {
        val exportDir = File(context.filesDir, EXPORT_DIR)
        if (!exportDir.exists()) exportDir.mkdirs()

        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val exportFile = File(exportDir, "mahasiswa_$timestamp.csv")

        FileWriter(exportFile).use { writer ->
            writer.appendLine("No,Nama,NIM,Program Studi,Email,Semester")

            students.forEachIndexed { index, student ->
                writer.appendLine("${index + 1},${student.name},${student.nim},${student.prodi},${student.email},${student.semester}")
            }
        }
        return exportFile
    }

    fun backupData(students: List<StudentEntity>) {
        val backupFile = File(context.filesDir, BACKUP_FILENAME)

        FileWriter(backupFile).use { writer ->
            students.forEach { student ->
                writer.appendLine("${student.id}|${student.name}|${student.nim}|${student.prodi}|${student.email}|${student.semester}|${student.createdAt}")
            }
        }
    }

    fun restoreData(): List<StudentEntity> {
        val backupFile = File(context.filesDir, BACKUP_FILENAME)
        if (!backupFile.exists()) return emptyList()

        val students = mutableListOf<StudentEntity>()

        backupFile.forEachLine { line ->
            val parts = line.split("|")
            if (parts.size == 7) {
                try {
                    students.add(
                        StudentEntity(
                            id = parts[0].toInt(),
                            name = parts[1],
                            nim = parts[2],
                            prodi = parts[3],
                            email = parts[4],
                            semester = parts[5].toInt(),
                            createdAt = parts[6].toLong()
                        )
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return students
    }

    fun isBackupAvailable(): Boolean {
        return File(context.filesDir, BACKUP_FILENAME).exists()
    }

    fun getExportedFiles(): List<File> {
        val exportDir = File(context.filesDir, EXPORT_DIR)
        return if (exportDir.exists()) {
            exportDir.listFiles()?.sortedByDescending { it.lastModified() } ?: emptyList()
        } else emptyList()
    }

    fun shareFile(file: File): Intent {
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
        return Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }
}