package com.android.hoang.chatapplication.ui.main.profile

import android.content.*
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import com.android.hoang.chatapplication.R
import com.android.hoang.chatapplication.base.BaseFragment
import com.android.hoang.chatapplication.data.remote.model.UserFirebase
import com.android.hoang.chatapplication.databinding.FragmentProfileBinding
import com.android.hoang.chatapplication.ui.auth.AuthActivity
import com.android.hoang.chatapplication.ui.main.MainActivity
import com.android.hoang.chatapplication.ui.main.MainActivityViewModel
import com.android.hoang.chatapplication.ui.main.profile.editprofile.EditProfileFragmentViewModel
import com.android.hoang.chatapplication.util.Constants
import com.android.hoang.chatapplication.util.Constants.LOG_TAG
import com.android.hoang.chatapplication.util.Status
import com.android.hoang.chatapplication.util.showMessage
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.StringUtils
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>() {

    private val profileViewModel: ProfileFragmentViewModel by viewModels()
    private val mainViewModel: MainActivityViewModel by activityViewModels()
    private var sharedPref: SharedPreferences? = null

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentProfileBinding.inflate(inflater, container, false)

    override fun prepareView(savedInstanceState: Bundle?) {
        val mainActivity = activity as MainActivity
        val appVersionName = mainActivity.packageManager.getPackageInfo(mainActivity.packageName, 0).versionName
        binding.txtAppVersion.text = appVersionName

        observeModel()

        binding.layoutLogout.setOnClickListener {
            requireContext().showMessage(R.string.question_log_out){
                profileViewModel.signOut()
                val intent = Intent(requireContext(), AuthActivity::class.java)
                startActivity(intent)
                (activity as MainActivity).finishAffinity()
            }
        }

        binding.editLanguage.setOnClickListener{
            findNavController().navigate(R.id.action_profileFragment_to_editLanguageFragment)
        }

        sharedPref = activity?.getSharedPreferences("current_language", Context.MODE_PRIVATE)
        val currentLang = sharedPref?.getString("current_language", "")

        if (currentLang != null){
            if (currentLang == "vi"){
                binding.txtCurrentLanguage.text = StringUtils.getString(R.string.vietnamese)
            } else {
                binding.txtCurrentLanguage.text = StringUtils.getString(R.string.english)

            }
        }
    }

    private fun observeModel() {
        profileViewModel.user.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    it.data?.let { currentUser ->
                        LogUtils.d("$this SUCCESS")
                        // ui
                        loadUserProfile(currentUser)
                        binding.btnEditProfile.setOnClickListener {
                            goToEditProfile(currentUser)
                        }

                    }
                    hideLoading()
                }
                Status.LOADING -> {
                    LogUtils.d("$this LOADING")
                    showLoading()
                }
                Status.ERROR -> {
                    LogUtils.d("$this ERROR")
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    hideLoading()
                }
            }
        }

        mainViewModel.updateProfileStatus.observe(viewLifecycleOwner){
            if (it){
                profileViewModel.getCurrentUser()
                mainViewModel.updateProfileStatus(false)
            }
        }
    }

    private fun loadUserProfile(currentUser: UserFirebase) {
        val errorImage = if (currentUser.imageUrl == "") R.drawable.avt_default else R.drawable.no_image
        Glide.with(requireContext()).load(currentUser.imageUrl).error(errorImage).into(binding.userAvt)
        Glide.with(requireContext()).load(currentUser.imageUrl).error(errorImage).into(binding.circleAvt)
        binding.userEmail.text = currentUser.email
        binding.userName.text = currentUser.username

    }

    private fun goToEditProfile(currentUser: UserFirebase){
        val action = ProfileFragmentDirections.actionProfileFragmentToEditProfileFragment(currentUser)
        findNavController().navigate(action)
    }


}