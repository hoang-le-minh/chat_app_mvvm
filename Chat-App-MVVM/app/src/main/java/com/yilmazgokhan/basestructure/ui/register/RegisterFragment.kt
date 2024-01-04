package com.yilmazgokhan.basestructure.ui.register

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.yilmazgokhan.basestructure.R
import com.yilmazgokhan.basestructure.base.BaseFragment
import com.yilmazgokhan.basestructure.databinding.FragmentRegisterBinding
import com.yilmazgokhan.basestructure.ui.auth.AuthActivity

class RegisterFragment : BaseFragment<FragmentRegisterBinding>(R.layout.fragment_register) {
    override fun prepareView(savedInstanceState: Bundle?) {
        val activity = activity as AuthActivity
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = activity.window
            window.statusBarColor = context?.let { ContextCompat.getColor(it, R.color.white) }!!
        }
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentRegisterBinding.inflate(inflater, container, false)

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