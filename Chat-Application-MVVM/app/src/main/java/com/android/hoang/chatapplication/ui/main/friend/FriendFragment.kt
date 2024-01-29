package com.android.hoang.chatapplication.ui.main.friend

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.android.hoang.chatapplication.R
import com.android.hoang.chatapplication.base.BaseFragment
import com.android.hoang.chatapplication.data.remote.model.UserFirebase
import com.android.hoang.chatapplication.databinding.FragmentFriendBinding
import com.android.hoang.chatapplication.ui.main.MainActivityViewModel
import com.android.hoang.chatapplication.ui.main.friend.friendviewpager.FriendViewPagerAdapter
import com.android.hoang.chatapplication.ui.main.friend.friendviewpager.alluser.AllUserFragment
import com.android.hoang.chatapplication.ui.main.friend.friendviewpager.friendrequest.FriendRequestFragment
import com.android.hoang.chatapplication.ui.main.friend.friendviewpager.friendrequest.FriendRequestFragmentViewModel
import com.android.hoang.chatapplication.ui.main.friend.friendviewpager.listfriend.ListFriendFragment
import com.android.hoang.chatapplication.util.Constants.LOG_TAG
import com.android.hoang.chatapplication.util.Status
import com.blankj.utilcode.util.LogUtils
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FriendFragment : BaseFragment<FragmentFriendBinding>() {

    //region vars
    private val viewModel: FriendFragmentViewModel by viewModels()
    private val friendRequestViewModel: FriendRequestFragmentViewModel by viewModels()
    private val mainViewModel: MainActivityViewModel by activityViewModels()
    //endregion

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentFriendBinding.inflate(inflater, container, false)

    override fun prepareView(savedInstanceState: Bundle?) {
        LogUtils.d("$this prepareView")
        val fragmentList = listOf(
            ListFriendFragment(),
            AllUserFragment(),
            FriendRequestFragment()
        )
        val viewPagerAdapter = FriendViewPagerAdapter(fragmentList, childFragmentManager, lifecycle)
        val viewPager = binding.friendViewPager
        viewPager.adapter = viewPagerAdapter

        initTabLayout(viewPager)

    }


    private fun initTabLayout(viewPager: ViewPager2){
        val tabLayout = binding.friendTabLayout

        TabLayoutMediator(tabLayout, viewPager){ tab, position ->
            tab.text = when(position){
                0 -> "BẠN BÈ"
                1 -> "TẤT CẢ"
                2 -> "YÊU CẦU"
                else -> "UNKNOWN"
            }
        }.attach()

        for(i in 0 until tabLayout.tabCount){
            val tab = tabLayout.getTabAt(i)
            if(tab != null){
                val customView = LayoutInflater.from(requireContext()).inflate(R.layout.custom_friend_tab_layout, null)

                val tabText = customView.findViewById<TextView>(R.id.tab_text)
                val countRequest = customView.findViewById<TextView>(R.id.txt_request_count)

                if(i != tabLayout.tabCount-1) countRequest.visibility = View.GONE

                setRequestCount(countRequest)
                mainViewModel.updateRequestCount.observe(viewLifecycleOwner){
                    if (it){
                        friendRequestViewModel.getRequestIdList()
                        mainViewModel.updateRequestCount(false)
                    }
                }

                if(tab.isSelected){
                    tabText.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_1))
                }

                tabText.text = tab.text
                tab.customView = customView
            }
        }

        tabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val customView = tab?.customView
                customView?.let {
                    val tabText = customView.findViewById<TextView>(R.id.tab_text)
                    tabText.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_1))
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                val customView = tab?.customView
                customView?.let {
                    val tabText = customView.findViewById<TextView>(R.id.tab_text)
                    tabText.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray))
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })
    }

    private fun setRequestCount(countRequest: TextView) {
        friendRequestViewModel.getRequestIdList()
        friendRequestViewModel.listIdRequest.observe(viewLifecycleOwner){
            when(it.status){
                Status.LOADING -> {
//                    showLoading()
                }
                Status.SUCCESS -> {
                    it.data?.let { list ->
//                        Log.d(Constants.LOG_TAG, "friendRequest.initListRequestUser: ${list[0]}")
                        countRequest.text = list.size.toString()
                    }
                    hideLoading()
                }
                Status.ERROR -> {
                    it.message.let { msg ->
//                        Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
                        hideLoading()
                    }
                }
            }
        }

        mainViewModel.requestCount.observe(viewLifecycleOwner){
            countRequest.text = it.toString()
        }
    }
}