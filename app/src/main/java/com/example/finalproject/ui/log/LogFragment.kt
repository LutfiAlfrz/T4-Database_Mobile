package com.example.finalproject.ui.log

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.finalproject.databinding.FragmentLogBinding
import com.example.finalproject.storage.ActivityLogger

class LogFragment : Fragment() {
    private var _binding: FragmentLogBinding? = null
    private val binding get() = _binding!!
    private lateinit var logger: ActivityLogger

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        logger = ActivityLogger(requireContext())

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        loadLog()

        binding.btnClearLog.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Hapus Log?")
                .setMessage("Semua riwayat aktivitas akan dihapus. Lanjutkan?")
                .setPositiveButton("Hapus") { _, _ ->
                    logger.clearLog()
                    loadLog()
                }
                .setNegativeButton("Batal", null)
                .show()
        }
    }

    private fun loadLog() {
        val lines = logger.readLogLines()
        if (lines.isEmpty()) {
            binding.tvLog.text = "Belum ada aktivitas tercatat."
            binding.tvLogCount.text = "0 aktivitas"
        } else {
            binding.tvLog.text = lines.joinToString("\n")
            binding.tvLogCount.text = "${lines.size} aktivitas"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}