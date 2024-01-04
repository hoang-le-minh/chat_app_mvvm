package com.yilmazgokhan.basestructure.ui.home

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.LogUtils
import com.yilmazgokhan.basestructure.R
import com.yilmazgokhan.basestructure.base.BaseFragment
import com.yilmazgokhan.basestructure.data.remote.model.UserResponse
import com.yilmazgokhan.basestructure.databinding.FragmentHomeBinding
import com.yilmazgokhan.basestructure.ui.auth.AuthActivity
import com.yilmazgokhan.basestructure.ui.main.MainActivity
import com.yilmazgokhan.basestructure.util.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.custom_message_item_view.view.*
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home) {

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
        val activity = activity as MainActivity
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = activity.window
            window.statusBarColor = context?.let { ContextCompat.getColor(it, R.color.color_1) }!!
        }
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
    private fun prepareComponents(user: UserResponse?) {
        Toast.makeText(context, "login: ${user?.login} \n id: ${user?.id} \n nodeId: ${user?.nodeId}", Toast.LENGTH_LONG).show()
        val recyclerView = binding.recyclerviewMessage
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        listMessageAdapter.submitList(viewModel.listUserTest)
        recyclerView.adapter = listMessageAdapter

    }

}