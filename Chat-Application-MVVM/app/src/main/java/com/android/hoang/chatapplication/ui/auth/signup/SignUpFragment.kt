package com.android.hoang.chatapplication.ui.auth.signup

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.android.hoang.chatapplication.R
import com.android.hoang.chatapplication.base.BaseFragment
import com.android.hoang.chatapplication.databinding.FragmentSignUpBinding
import com.android.hoang.chatapplication.ui.auth.AuthActivity
import com.android.hoang.chatapplication.util.Constants.LOG_TAG
import com.android.hoang.chatapplication.util.Status
import com.blankj.utilcode.util.LogUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlin.math.sign

@AndroidEntryPoint
class RegisterFragment : BaseFragment<FragmentSignUpBinding>() {

    private val signUpViewModel: SignUpFragmentViewModel by viewModels()

    override fun prepareView(savedInstanceState: Bundle?) {
//        val activity = activity as AuthActivity
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            val window: Window = activity.window
//            window.statusBarColor = context?.let { ContextCompat.getColor(it, R.color.white) }!!
//        }

        with(binding){
            txtLogin.setOnClickListener { navigateToLogin() }
            btnBack.setOnClickListener { navigateToLogin() }
            btnRegister.setOnClickListener { userRegister() }
        }

        updateUiButtonSignUp()
    }

    private fun updateUiButtonSignUp() {
        binding.signUpViewModel = signUpViewModel
        binding.lifecycleOwner = this
        signUpViewModel.name.observe(viewLifecycleOwner){
            signUpViewModel.updateSignUpButtonState()
        }
        signUpViewModel.email.observe(viewLifecycleOwner){
            signUpViewModel.updateSignUpButtonState()
        }
        signUpViewModel.password.observe(viewLifecycleOwner){
            signUpViewModel.updateSignUpButtonState()
        }
        signUpViewModel.isAcceptChecked.observe(viewLifecycleOwner){
            signUpViewModel.updateSignUpButtonState()
        }
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentSignUpBinding.inflate(inflater, container, false)


    private fun userRegister() {
        val name = binding.edtFullName.text.toString()
        val email = binding.edtEmail.text.toString().trim()
        val password = binding.edtPassword.text.toString().trim()
        signUpViewModel.signUp(email, password)
        signUpViewModel.result.observe(viewLifecycleOwner){
            when (it.status) {
                Status.SUCCESS -> {
                    it.data.let {
                        LogUtils.d("$this SUCCESS")
                    }
                    navigateToLogin()
                    Toast.makeText(requireContext(), "Đăng ký thành công", Toast.LENGTH_LONG).show()
                    hideLoading()
                }
                Status.LOADING -> {
                    LogUtils.d("$this LOADING")
                    showLoading()
                }
                Status.ERROR -> {
                    LogUtils.d("$this ERROR")
                    it.message.let { msg ->
                        binding.txtSignUpResult.text = msg
                        Log.d(LOG_TAG, "userRegister: $msg")
                    }
                    hideLoading()
                }
            }
        }
    }

    private fun navigateToLogin() {
        findNavController().popBackStack()
    }

}