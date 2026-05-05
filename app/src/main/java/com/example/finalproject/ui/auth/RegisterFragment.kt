package com.example.finalproject.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.finalproject.R
import com.example.finalproject.databinding.FragmentRegisterBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObserver()
        binding.btnRegister.setOnClickListener {
            viewModel.register(
                nama = binding.etNama.text.toString(),
                username = binding.etUsername.text.toString(),
                email = binding.etEmail.text.toString(),
                password = binding.etPassword.text.toString(),
                konfirmasiPassword = binding.etKonfirmasiPassword.text.toString()
            )
        }

        binding.tvLogin.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.registerResult.collectLatest { result ->
                when (result) {
                    is AuthResult.Loading -> {
                        binding.btnRegister.isEnabled = false
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is AuthResult.Success -> {
                        binding.progressBar.visibility = View.GONE
                        Snackbar.make(binding.root, "Registrasi berhasil! Silakan login.", Snackbar.LENGTH_SHORT).show()
                        findNavController().navigateUp()
                        viewModel.resetRegisterResult()
                    }
                    is AuthResult.Error -> {
                        binding.btnRegister.isEnabled = true
                        binding.progressBar.visibility = View.GONE
                        Snackbar.make(binding.root, result.message, Snackbar.LENGTH_SHORT).show()
                        viewModel.resetRegisterResult()
                    }
                    else -> {
                        binding.btnRegister.isEnabled = true
                        binding.progressBar.visibility = View.GONE
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}