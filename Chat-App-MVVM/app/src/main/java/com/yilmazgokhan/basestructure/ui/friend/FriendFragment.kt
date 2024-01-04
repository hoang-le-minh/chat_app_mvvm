package com.yilmazgokhan.basestructure.ui.friend

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.blankj.utilcode.util.LogUtils
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayoutMediator
import com.yilmazgokhan.basestructure.R
import com.yilmazgokhan.basestructure.base.BaseFragment
import com.yilmazgokhan.basestructure.databinding.FragmentFriendBinding
import com.yilmazgokhan.basestructure.ui.friend.friendviewpager.AllUserFragment
import com.yilmazgokhan.basestructure.ui.friend.friendviewpager.FriendRequestFragment
import com.yilmazgokhan.basestructure.ui.friend.friendviewpager.FriendViewPagerAdapter
import com.yilmazgokhan.basestructure.ui.friend.friendviewpager.ListFriendFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * TODO("The class will delete when new tabs add to project")
 *
 * The class created for Bottom Menu friend
 */
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class FriendFragment : BaseFragment<FragmentFriendBinding>(R.layout.fragment_friend) {

    //region vars
    private val viewModel: FriendFragmentViewModel by viewModels()
    //endregion

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

                if(tab.isSelected){
                    tabText.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_1))
                }

                tabText.text = tab.text
                tab.customView = customView
            }
        }

        tabLayout.addOnTabSelectedListener(object: OnTabSelectedListener{
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

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentFriendBinding.inflate(inflater, container, false)
}