package com.android.hoang.chatapplication.ui.main.home

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.hoang.chatapplication.R
import com.android.hoang.chatapplication.base.BaseFragment
import com.android.hoang.chatapplication.data.remote.model.UserFirebase
import com.android.hoang.chatapplication.data.remote.model.UserResponse
import com.android.hoang.chatapplication.databinding.FragmentHomeBinding
import com.android.hoang.chatapplication.ui.main.MainActivity
import com.android.hoang.chatapplication.util.Status
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.StringUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    //region vars
    private val viewModel: HomeFragmentViewModel by viewModels()
    private val listMessageAdapter = ListUserMessageAdapter()
    //endregion

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentHomeBinding.inflate(inflater, container, false)

    override fun prepareView(savedInstanceState: Bundle?) {
        LogUtils.d("$this prepareView")
        this.observeModel()
//        val activity = activity as MainActivity
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            val window: Window = activity.window
//            window.statusBarColor = context?.let { ContextCompat.getColor(it, R.color.color_1) }!!
//        }
    }

    /**
     * Observe LiveData models
     */
    private fun observeModel() {
        viewModel.user.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    it.data.let {
                        LogUtils.d("$this SUCCESS")
                        prepareComponents(it)
                    }
                    hideLoading()
                }
                Status.LOADING -> {
                    LogUtils.d("$this LOADING")
                    showLoading()
                }
                Status.ERROR -> {
                    LogUtils.d("$this ERROR")
                    showLoading()
                }
            }
        }
    }

    /**
     * Prepare components & show data in UI
     *
     */
    private fun prepareComponents(user: UserFirebase?) {
        val recyclerView = binding.recyclerviewMessage
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        listMessageAdapter.submitList(viewModel.listUserTest)
        recyclerView.adapter = listMessageAdapter

    }

}