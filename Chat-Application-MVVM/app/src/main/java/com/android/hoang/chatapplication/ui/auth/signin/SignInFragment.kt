package com.android.hoang.chatapplication.ui.auth.signin

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.android.hoang.chatapplication.R
import com.android.hoang.chatapplication.base.BaseFragment
import com.android.hoang.chatapplication.databinding.FragmentSignInBinding
import com.android.hoang.chatapplication.ui.auth.AuthActivity
import com.android.hoang.chatapplication.ui.main.MainActivity
import com.android.hoang.chatapplication.util.Constants.LOG_TAG
import com.android.hoang.chatapplication.util.Status
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignInFragment : BaseFragment<FragmentSignInBinding>() {

    private val viewModel: SignInFragmentViewModel by viewModels()

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentSignInBinding.inflate(inflater, container, false)

    private lateinit var auth: FirebaseAuth

    override fun prepareView(savedInstanceState: Bundle?) {
//        val activity = activity as AuthActivity
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            val window: Window = activity.window
//            window.statusBarColor = context?.let { ContextCompat.getColor(it, R.color.white) }!!
//        }

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(LOG_TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            Log.d(LOG_TAG, "onViewCreated: $token")
        })

        // log out
        auth = FirebaseAuth.getInstance()
        auth.signOut()
        updateUiButtonSignIn()

        binding.btnLogin.setOnClickListener {
            onClickLogin()
        }

        binding.txtRegister.setOnClickListener {
            navigateToRegister()
        }
    }

    private fun updateUiButtonSignIn() {
        binding.loginFragmentViewModel = viewModel
        binding.lifecycleOwner = this
        // update login button state
        viewModel.email.observe(viewLifecycleOwner){
            viewModel.updateLoginButtonState()
        }
        viewModel.password.observe(viewLifecycleOwner){
            viewModel.updateLoginButtonState()
        }

    }

    private fun navigateToRegister() {
        findNavController().navigate(R.id.action_signInFragment_to_registerFragment)
    }

    private fun onClickLogin() {
        val email = binding.edtEmail.text.toString().trim()
        val password = binding.edtPassword.text.toString().trim()
        viewModel.signIn(email, password)
        viewModel.result.observe(viewLifecycleOwner){
            when(it.status){
                Status.LOADING -> showLoading()
                Status.SUCCESS -> {
                    val intent = Intent(context, MainActivity::class.java)
                    startActivity(intent)
                    val activity = activity as AuthActivity
                    activity.finish()
                    hideLoading()
                }
                Status.ERROR -> {
                    it.message.let { msg ->
                        binding.txtSignInResult.text = msg
                        hideLoading()
                    }
                }
            }
        }
    }
}