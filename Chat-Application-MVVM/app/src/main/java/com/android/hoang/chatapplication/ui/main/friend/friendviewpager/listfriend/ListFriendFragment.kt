package com.android.hoang.chatapplication.ui.main.friend.friendviewpager.listfriend

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.hoang.chatapplication.R
import com.android.hoang.chatapplication.base.BaseFragment
import com.android.hoang.chatapplication.databinding.FragmentListFriendBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListFriendFragment : BaseFragment<FragmentListFriendBinding>() {
    override fun prepareView(savedInstanceState: Bundle?) {

    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentListFriendBinding.inflate(inflater, container, false)

}