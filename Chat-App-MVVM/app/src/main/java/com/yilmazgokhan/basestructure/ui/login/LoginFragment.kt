package com.yilmazgokhan.basestructure.ui.login

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.yilmazgokhan.basestructure.R
import com.yilmazgokhan.basestructure.base.BaseFragment
import com.yilmazgokhan.basestructure.databinding.FragmentLoginBinding
import com.yilmazgokhan.basestructure.ui.auth.AuthActivity
import com.yilmazgokhan.basestructure.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(R.layout.fragment_login) {

    private val viewModel: LoginFragmentViewModel by viewModels()

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentLoginBinding.inflate(inflater, container, false)

    override fun prepareView(savedInstanceState: Bundle?) {
        val activity = activity as AuthActivity
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = activity.window
            window.statusBarColor = context?.let { ContextCompat.getColor(it, R.color.white) }!!
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loginFragmentViewModel = viewModel
        binding.lifecycleOwner = this
        // update login button state
        viewModel.email.observe(viewLifecycleOwner){
            viewModel.updateLoginButtonState()
        }
        viewModel.password.observe(viewLifecycleOwner){
            viewModel.updateLoginButtonState()
        }

        binding.btnLogin.setOnClickListener {
            login()
        }

        binding.txtRegister.setOnClickListener {
            navigateToRegister()
        }
    }

    private fun navigateToRegister() {
        findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
    }

    private fun login() {
        val intent = Intent(context, MainActivity::class.java)
        startActivity(intent)
        val activity = activity as AuthActivity
        activity.finish()
    }
}