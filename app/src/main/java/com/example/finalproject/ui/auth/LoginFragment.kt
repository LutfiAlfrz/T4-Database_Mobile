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
import com.example.finalproject.databinding.FragmentLoginBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (viewModel.session.isLoggedIn) {
            navigateToHome()
            return
        }
        setupObserver()

        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text.toString()
            val password = binding.etPassword.text.toString()
            viewModel.login(username, password)
        }

        binding.tvRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    private fun setupObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loginResult.collectLatest { result ->
                when (result) {
                    is AuthResult.Loading -> {
                        binding.btnLogin.isEnabled = false
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is AuthResult.Success -> {
                        binding.progressBar.visibility = View.GONE
                        navigateToHome()
                        viewModel.resetLoginResult()
                    }
                    is AuthResult.Error -> {
                        binding.btnLogin.isEnabled = true
                        binding.progressBar.visibility = View.GONE
                        Snackbar.make(binding.root, result.message, Snackbar.LENGTH_SHORT).show()
                        viewModel.resetLoginResult()
                    }
                    else -> {
                        binding.btnLogin.isEnabled = true
                        binding.progressBar.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun navigateToHome() {
        findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}