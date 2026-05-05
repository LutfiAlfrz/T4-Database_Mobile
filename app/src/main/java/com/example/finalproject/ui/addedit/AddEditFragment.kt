package com.example.finalproject.ui.addedit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.finalproject.database.AppDatabase
import com.example.finalproject.database.entity.StudentEntity
import com.example.finalproject.databinding.FragmentAddEditBinding
import com.example.finalproject.storage.ActivityLogger
import kotlinx.coroutines.launch

class AddEditFragment : Fragment() {
    private var _binding: FragmentAddEditBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: AppDatabase
    private lateinit var logger: ActivityLogger
    private var studentId: Int = -1
    private var existingStudent: StudentEntity? = null

    private val prodiList = listOf(
        "Teknik Informatika", "Sistem Informasi", "Teknik Elektro",
        "Manajemen Informatika", "Teknik Komputer", "Ilmu Komputer"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = AppDatabase.getInstance(requireContext())
        logger = ActivityLogger(requireContext())
        studentId = arguments?.getInt("studentId", -1) ?: -1

        setupSpinner()
        setupToolbar()

        if (studentId != -1) loadStudentData()
        else binding.toolbar.title = "Tambah Mahasiswa"

        binding.btnSave.setOnClickListener {
            if (validateInput()) saveStudent()
        }
    }

    private fun setupSpinner() {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, prodiList)
            .also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        binding.spinnerProdi.adapter = adapter
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
    }

    private fun loadStudentData() {
        binding.toolbar.title = "Edit Mahasiswa"
        viewLifecycleOwner.lifecycleScope.launch {
            existingStudent = db.studentDao().getStudentById(studentId)
            existingStudent?.let {
                binding.etName.setText(it.name)
                binding.etNim.setText(it.nim)
                binding.etEmail.setText(it.email)
                binding.etSemester.setText(it.semester.toString())
                val prodiIndex = prodiList.indexOf(it.prodi)
                if (prodiIndex >= 0) binding.spinnerProdi.setSelection(prodiIndex)
            }
        }
    }

    private fun validateInput(): Boolean {
        val name = binding.etName.text.toString().trim()
        val nim = binding.etNim.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val semester = binding.etSemester.text.toString().trim()

        if (name.isEmpty()) { binding.etName.error = "Nama tidak boleh kosong"; binding.etName.requestFocus(); return false }
        if (nim.isEmpty()) { binding.etNim.error = "NIM tidak boleh kosong"; binding.etNim.requestFocus(); return false }
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) { binding.etEmail.error = "Email tidak valid"; binding.etEmail.requestFocus(); return false }
        if (semester.isEmpty() || semester.toIntOrNull() == null) { binding.etSemester.error = "Semester harus angka"; binding.etSemester.requestFocus(); return false }
        if (semester.toInt() < 1 || semester.toInt() > 14) { binding.etSemester.error = "Semester 1-14"; binding.etSemester.requestFocus(); return false }
        return true
    }

    private fun saveStudent() {
        val name = binding.etName.text.toString().trim()
        val nim = binding.etNim.text.toString().trim()
        val prodi = binding.spinnerProdi.selectedItem.toString()
        val email = binding.etEmail.text.toString().trim()
        val semester = binding.etSemester.text.toString().trim().toInt()

        viewLifecycleOwner.lifecycleScope.launch {
            if (studentId == -1) {
                // Mode TAMBAH
                db.studentDao().insert(StudentEntity(name = name, nim = nim, prodi = prodi, email = email, semester = semester))
                logger.log(ActivityLogger.ACTION_ADD, "Mahasiswa \"$name\" (NIM: $nim) ditambahkan")
                Toast.makeText(requireContext(), "Mahasiswa berhasil ditambahkan!", Toast.LENGTH_SHORT).show()
            } else {
                // Mode EDIT
                val updated = existingStudent!!.copy(name = name, nim = nim, prodi = prodi, email = email, semester = semester)
                db.studentDao().update(updated)
                logger.log(ActivityLogger.ACTION_EDIT, "Data \"$name\" (NIM: $nim) diperbarui")
                Toast.makeText(requireContext(), "Data berhasil diperbarui!", Toast.LENGTH_SHORT).show()
            }
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}