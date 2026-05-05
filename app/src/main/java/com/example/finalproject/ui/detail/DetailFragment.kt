package com.example.finalproject.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.finalproject.R
import com.example.finalproject.database.AppDatabase
import com.example.finalproject.databinding.FragmentDetailBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DetailFragment : Fragment() {
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val studentId = arguments?.getInt("studentId", -1) ?: -1
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        if (studentId != -1) {
            loadStudentDetail(studentId)
        }

        binding.btnEdit.setOnClickListener {
            val bundle = Bundle().apply { putInt("studentId", studentId) }
            findNavController().navigate(R.id.action_detailFragment_to_addEditFragment, bundle)
        }
    }

    private fun loadStudentDetail(studentId: Int) {
        viewLifecycleOwner.lifecycleScope.launch {
            val db = AppDatabase.getInstance(requireContext())
            val student = db.studentDao().getStudentById(studentId)

            student?.let {
                val initials = it.name.split(" ")
                    .take(2)
                    .joinToString("") { word -> word.first().uppercase() }
                binding.tvInitials.text = initials

                val colors = listOf(
                    0xFF5C6BC0.toInt(), 0xFF26A69A.toInt(), 0xFFEF5350.toInt(),
                    0xFFAB47BC.toInt(), 0xFF42A5F5.toInt(), 0xFFFF7043.toInt()
                )
                val colorIndex = (it.name.hashCode() and 0x7FFFFFFF) % colors.size
                binding.avatarCircle.setBackgroundColor(colors[colorIndex])

                binding.tvName.text = it.name
                binding.tvNim.text = it.nim
                binding.tvProdi.text = it.prodi
                binding.tvEmail.text = it.email
                binding.tvSemester.text = "Semester ${it.semester}"

                val dateFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale("id"))
                binding.tvCreatedAt.text = "Ditambahkan: ${dateFormat.format(Date(it.createdAt))}"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}