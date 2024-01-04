package com.android.hoang.chatapplication.ui.auth.signup

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.android.hoang.chatapplication.R
import com.android.hoang.chatapplication.base.BaseFragment
import com.android.hoang.chatapplication.databinding.FragmentSignUpBinding
import com.android.hoang.chatapplication.ui.auth.AuthActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : BaseFragment<FragmentSignUpBinding>() {
    override fun prepareView(savedInstanceState: Bundle?) {
//        val activity = activity as AuthActivity
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            val window: Window = activity.window
//            window.statusBarColor = context?.let { ContextCompat.getColor(it, R.color.white) }!!
//        }
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentSignUpBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding){
            txtLogin.setOnClickListener { navigateToLogin() }
            btnBack.setOnClickListener { navigateToLogin() }
            btnRegister.setOnClickListener { userRegister() }
        }
    }

    private fun userRegister() {


        navigateToLogin()
    }

    private fun navigateToLogin() {
        findNavController().popBackStack()
    }

}