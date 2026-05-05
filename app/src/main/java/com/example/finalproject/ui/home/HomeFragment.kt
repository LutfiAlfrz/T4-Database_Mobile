package com.example.finalproject.ui.home

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalproject.R
import com.example.finalproject.adapter.StudentAdapter
import com.example.finalproject.databinding.FragmentHomeBinding
import com.example.finalproject.preferences.AppPreferences
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var studentAdapter: StudentAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)

        setupRecyclerView()
        setupSwipeToDelete()
        observeViewModel()

        binding.fabAdd.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_addEditFragment)
        }
    }

    private fun setupRecyclerView() {
        studentAdapter = StudentAdapter(
            onItemClick = { student ->
                val bundle = Bundle().apply { putInt("studentId", student.id) }
                findNavController().navigate(R.id.action_homeFragment_to_detailFragment, bundle)
            },
            onEditClick = { student ->
                val bundle = Bundle().apply { putInt("studentId", student.id) }
                findNavController().navigate(R.id.action_homeFragment_to_addEditFragment, bundle)
            },
            onDeleteClick = { student ->
                showDeleteDialog(student.id, student.name)
            }
        )
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = studentAdapter
        }
    }

    private fun setupSwipeToDelete() {
        SwipeToDeleteHelper(
            adapter = studentAdapter,
            onSwipedConfirmed = { student, position ->
                showDeleteDialogWithRestore(student.id, student.name, position)
            }
        ).build().attachToRecyclerView(binding.recyclerView)
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            launch {
                viewModel.getStudents().collectLatest { students ->
                    studentAdapter.submitList(students)
                    binding.tvEmpty.visibility = if (students.isEmpty()) View.VISIBLE else View.GONE
                    binding.recyclerView.visibility = if (students.isEmpty()) View.GONE else View.VISIBLE
                }
            }
            launch {
                viewModel.snackbarMessage.collectLatest { message ->
                    message?.let {
                        Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT).show()
                        viewModel.clearSnackbar()
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_sort -> { showSortDialog(); true }
            R.id.menu_log -> {
                findNavController().navigate(R.id.action_homeFragment_to_logFragment)
                true
            }
            R.id.menu_logout -> { showLogoutDialog(); true }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showSortDialog() {
        val options = arrayOf("Nama (A-Z)", "NIM", "Semester")
        val sortValues = arrayOf(AppPreferences.SORT_BY_NAME, AppPreferences.SORT_BY_NIM, AppPreferences.SORT_BY_SEMESTER)
        val currentIndex = sortValues.indexOf(viewModel.prefs.sortOrder).coerceAtLeast(0)
        AlertDialog.Builder(requireContext())
            .setTitle("Urutkan Berdasarkan")
            .setSingleChoiceItems(options, currentIndex) { dialog, which ->
                viewModel.saveSortOrder(sortValues[which])
                observeViewModel()
                dialog.dismiss()
                Snackbar.make(binding.root, "Diurutkan: ${options[which]}", Snackbar.LENGTH_SHORT).show()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Logout")
            .setMessage("Apakah kamu yakin ingin keluar?")
            .setPositiveButton("Logout") { _, _ ->
                viewModel.logout()
                findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun showDeleteDialog(id: Int, name: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Hapus Data?")
            .setMessage("Hapus \"$name\"? Tindakan ini tidak dapat dibatalkan.")
            .setPositiveButton("Hapus") { _, _ -> viewModel.deleteStudent(id, name) }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun showDeleteDialogWithRestore(id: Int, name: String, position: Int) {
        AlertDialog.Builder(requireContext())
            .setTitle("Hapus Data?")
            .setMessage("Hapus \"$name\"? Tindakan ini tidak dapat dibatalkan.")
            .setPositiveButton("Hapus") { _, _ -> viewModel.deleteStudent(id, name) }
            .setNegativeButton("Batal") { _, _ -> studentAdapter.notifyItemChanged(position) }
            .setOnCancelListener { studentAdapter.notifyItemChanged(position) }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}