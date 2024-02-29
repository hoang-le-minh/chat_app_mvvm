package com.android.hoang.chatapplication.ui.main.profile.editlanguage

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.hoang.chatapplication.R
import com.android.hoang.chatapplication.base.BaseFragment
import com.android.hoang.chatapplication.databinding.FragmentEditLanguageBinding
import com.android.hoang.chatapplication.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditLanguageFragment : BaseFragment<FragmentEditLanguageBinding>() {

    private var sharedPref: SharedPreferences? = null

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentEditLanguageBinding.inflate(inflater, container, false)

    override fun prepareView(savedInstanceState: Bundle?) {
        sharedPref = activity?.getSharedPreferences("current_language", Context.MODE_PRIVATE)
        val currentLang = sharedPref?.getString("current_language", "")

        if (currentLang != null){
            if (currentLang == "vi"){
                binding.radioVietnamese.isChecked = true
            } else {
                binding.radioEnglish.isChecked = true
            }
        }

        binding.btnChangeLanguage.setOnClickListener {
            if (binding.radioVietnamese.isChecked){
                sharedPref?.edit()?.putString("current_language", "vi")?.apply()
                handleOpenApp()
            }
            if (binding.radioEnglish.isChecked){
                sharedPref?.edit()?.putString("current_language", "")?.apply()
                handleOpenApp()
            }
        }
    }

    private fun handleOpenApp() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        (activity as MainActivity).finishAffinity()
        startActivity(intent)
        (activity as MainActivity).recreate()

    }

}