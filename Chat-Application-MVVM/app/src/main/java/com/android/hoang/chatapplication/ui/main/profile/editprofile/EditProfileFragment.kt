package com.android.hoang.chatapplication.ui.main.profile.editprofile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.android.hoang.chatapplication.R
import com.android.hoang.chatapplication.base.BaseFragment
import com.android.hoang.chatapplication.databinding.FragmentEditProfileBinding
import com.android.hoang.chatapplication.util.Constants.LOG_TAG
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditProfileFragment : BaseFragment<FragmentEditProfileBinding>() {

    private val args: EditProfileFragmentArgs by navArgs()

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentEditProfileBinding.inflate(inflater, container, false)

    override fun prepareView(savedInstanceState: Bundle?) {
        Log.d(LOG_TAG, "editProfileFragment.prepareView: ${args.currentUser.id}")
    }


}