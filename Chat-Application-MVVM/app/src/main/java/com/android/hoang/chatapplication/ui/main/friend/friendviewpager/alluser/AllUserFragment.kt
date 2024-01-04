package com.android.hoang.chatapplication.ui.main.friend.friendviewpager.alluser

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.hoang.chatapplication.R
import com.android.hoang.chatapplication.base.BaseFragment
import com.android.hoang.chatapplication.databinding.FragmentAllUserBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AllUserFragment : BaseFragment<FragmentAllUserBinding>() {
    override fun prepareView(savedInstanceState: Bundle?) {

    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentAllUserBinding.inflate(inflater, container, false)

}