package com.example.mynotes.ui.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import com.example.mynotes.R
import com.example.mynotes.databinding.FragmentLoginBinding
import com.example.mynotes.models.UserRequest
import com.example.mynotes.utils.NetworkResult
import com.example.mynotes.utils.TokenManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class LoginFragment : Fragment() {


    private var _binding : FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val authViewModel by viewModels<AuthViewModel>()

    @Inject
    lateinit var tokenManager: TokenManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater,container,false)



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogin.setOnClickListener(View.OnClickListener {

            val validateUserInput = validateUserInput()
            if(validateUserInput.first)
            {
                authViewModel.loginUser(getUserRequest())
            }
            else
            {
                binding.txtError.text = validateUserInput.second
            }

        })

        binding.btnSignUp.setOnClickListener(View.OnClickListener {

            findNavController().popBackStack()
        })

        bindObservers()

    }

    private fun bindObservers() {
        authViewModel.userResponsLivedata.observe(viewLifecycleOwner,{
            binding.progressBar.isVisible = false

            when(it)
            {
                is NetworkResult.Success -> {

                    tokenManager.saveToken(it.data!!.token)
                    findNavController().navigate(R.id.action_loginFragment_to_mainFragment)
                }
                is NetworkResult.Error -> {

                    binding.txtError.text = it.message
                }
                is NetworkResult.Loading ->
                {
                    binding.progressBar.isVisible = true
                }
            }
        })
    }

    private fun getUserRequest() : UserRequest
    {
        val email = binding.txtEmail.text.toString()
        val password = binding.txtPassword.text.toString()
        return UserRequest(email,password,"")
    }


    private fun validateUserInput(): Pair<Boolean, String> {
        val userdata = getUserRequest()

        return authViewModel.validateCredentials(userdata.username,userdata.email,userdata.password,true)

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}