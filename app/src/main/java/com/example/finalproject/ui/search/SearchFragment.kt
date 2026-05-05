package com.example.finalproject.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalproject.R
import com.example.finalproject.adapter.StudentAdapter
import com.example.finalproject.database.AppDatabase
import com.example.finalproject.databinding.FragmentSearchBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var studentAdapter: StudentAdapter
    private lateinit var db: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = AppDatabase.getInstance(requireContext())

        setupToolbar()
        setupRecyclerView()
        setupSearchView()

        searchStudents("")
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupRecyclerView() {
        studentAdapter = StudentAdapter(
            onItemClick = { student ->
                // Klik item → ke halaman Detail
                val bundle = Bundle().apply { putInt("studentId", student.id) }
                findNavController().navigate(R.id.action_searchFragment_to_detailFragment, bundle)
            },
            onEditClick = { student ->
                val bundle = Bundle().apply { putInt("studentId", student.id) }
                findNavController().navigate(R.id.action_searchFragment_to_addEditFragment, bundle)
            },
            onDeleteClick = { student ->
                AlertDialog.Builder(requireContext())
                    .setTitle("Hapus Data?")
                    .setMessage("Hapus \"${student.name}\"? Tindakan ini tidak dapat dibatalkan.")
                    .setPositiveButton("Hapus") { _, _ ->
                        viewLifecycleOwner.lifecycleScope.launch {
                            db.studentDao().deleteById(student.id)
                        }
                    }
                    .setNegativeButton("Batal", null)
                    .show()
            }
        )
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = studentAdapter
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchStudents(query ?: "")
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                searchStudents(newText ?: "")
                return true
            }
        })
    }

    private fun searchStudents(keyword: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            val flow = if (keyword.isEmpty()) db.studentDao().getAllStudents()
            else db.studentDao().searchStudents(keyword)

            flow.collectLatest { students ->
                studentAdapter.submitList(students)
                if (students.isEmpty()) {
                    binding.tvEmpty.visibility = View.VISIBLE
                    binding.recyclerView.visibility = View.GONE
                    binding.tvEmpty.text = if (keyword.isEmpty()) "Belum ada data mahasiswa"
                    else "Tidak ditemukan: \"$keyword\""
                } else {
                    binding.tvEmpty.visibility = View.GONE
                    binding.recyclerView.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}