package com.yilmazgokhan.basestructure.ui.friend.friendviewpager

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.yilmazgokhan.basestructure.R
import com.yilmazgokhan.basestructure.base.BaseFragment
import com.yilmazgokhan.basestructure.databinding.FragmentAllUserBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class AllUserFragment : BaseFragment<FragmentAllUserBinding>(R.layout.fragment_all_user) {
    override fun prepareView(savedInstanceState: Bundle?) {

    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentAllUserBinding.inflate(inflater, container, false)

}