package com.example.mynotes.ui.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.mynotes.R
import com.example.mynotes.databinding.FragmentRegisterBinding
import com.example.mynotes.models.UserRequest
import com.example.mynotes.utils.NetworkResult
import com.example.mynotes.utils.TokenManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private var _binding : FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val authViewModel by viewModels<AuthViewModel>()

    @Inject
    lateinit var tokenManager : TokenManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRegisterBinding.inflate(inflater,container,false)

        if(tokenManager.getToken() != null)
        {
            findNavController().navigate(R.id.action_registerFragment_to_mainFragment)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.btnSignUp.setOnClickListener(View.OnClickListener {
            val validateResult = validateUserInput()
            if(validateResult.first)
            {
                authViewModel.registerUser(getUserRequest())
            }
            else
            {
                binding.txtError.text = validateResult.second
            }
        })


        binding.btnLogin.setOnClickListener(View.OnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        })

        bindObservers()

    }

    private fun getUserRequest() : UserRequest{
        val emailAddress = binding.txtEmail.text.toString()
        val password = binding.txtPassword.text.toString()
        val username = binding.txtUsername.text.toString()

        return UserRequest(emailAddress,password,username)
    }

    private fun validateUserInput(): Pair<Boolean, String> {

        val userRequest = getUserRequest()
        return authViewModel.validateCredentials(userRequest.username,userRequest.email,userRequest.password,false)
    }

    private fun bindObservers() {

        authViewModel.userResponsLivedata.observe(viewLifecycleOwner,{
            binding.progressBar.isVisible = false

            when(it)
            {
                is NetworkResult.Success -> {
                    tokenManager.saveToken(it.data!!.token)
                    findNavController().navigate(R.id.action_registerFragment_to_mainFragment)
                }
                is NetworkResult.Error -> {
                    binding.txtError.text = it.message
                }
                is NetworkResult.Loading -> {
                    binding.progressBar.isVisible = true
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}