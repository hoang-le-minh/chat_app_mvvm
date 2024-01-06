package com.android.hoang.chatapplication.ui.main.profile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.hoang.chatapplication.R
import com.android.hoang.chatapplication.base.BaseFragment
import com.android.hoang.chatapplication.databinding.FragmentProfileBinding
import com.android.hoang.chatapplication.ui.auth.AuthActivity
import com.android.hoang.chatapplication.ui.main.MainActivity
import com.android.hoang.chatapplication.util.showMessage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>() {

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentProfileBinding.inflate(inflater, container, false)

    override fun prepareView(savedInstanceState: Bundle?) {
        val mainActivity = activity as MainActivity
        val appVersionName = mainActivity.packageManager.getPackageInfo(mainActivity.packageName, 0).versionName
        binding.txtAppVersion.text = appVersionName

        binding.txtLogout.setOnClickListener {
            requireContext().showMessage(R.string.question_log_out){
                val intent = Intent(requireContext(), AuthActivity::class.java)
                startActivity(intent)
                (activity as MainActivity).finishAffinity()

            }
        }
    }

}