package com.android.hoang.chatapplication.ui.main.friend.friendviewpager.friendrequest

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.android.hoang.chatapplication.R
import com.android.hoang.chatapplication.base.BaseFragment
import com.android.hoang.chatapplication.databinding.FragmentFriendRequestBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FriendRequestFragment : BaseFragment<FragmentFriendRequestBinding>() {

    private val friendRequestViewModel: FriendRequestFragmentViewModel by viewModels()

    override fun prepareView(savedInstanceState: Bundle?) {

    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentFriendRequestBinding.inflate(inflater, container, false)

    private fun observerModel(){

    }

    private fun initListRequest(){

    }

    private fun initListSent(){

    }

}