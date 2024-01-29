package com.android.hoang.chatapplication.ui.main.friend.friendviewpager.listfriend

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.hoang.chatapplication.base.BaseFragment
import com.android.hoang.chatapplication.databinding.FragmentListFriendBinding
import com.android.hoang.chatapplication.ui.main.MainActivityViewModel
import com.android.hoang.chatapplication.util.Constants.LOG_TAG
import com.android.hoang.chatapplication.util.Status
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListFriendFragment : BaseFragment<FragmentListFriendBinding>() {

    private val listFriendViewModel: ListFriendFragmentViewModel by viewModels()
    private val mainViewModel: MainActivityViewModel by activityViewModels()

    override fun prepareView(savedInstanceState: Bundle?) {

        observerModel()

    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentListFriendBinding.inflate(inflater, container, false)

    private fun observerModel(){
        listFriendViewModel.getFriendList()
        listFriendViewModel.listIdFriend.observe(viewLifecycleOwner){
            when(it.status){
                Status.LOADING -> {}
                Status.SUCCESS -> {
                    it.data?.let { list ->
                        initListFriend(list)
                    }
//                    hideLoading()
                }
                Status.ERROR -> {
                    it.message.let { msg ->
//                        Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
                        hideLoading()
                    }
                }
            }
        }

        mainViewModel.updateListFriendStatus.observe(viewLifecycleOwner){
            if (it){
                listFriendViewModel.getFriendList()
                Log.d(LOG_TAG, "listFriend.observerModel: reload")
                mainViewModel.updateListFriendStatus(false)
            }
        }
    }

    private fun initListFriend(list: MutableList<String>){
        listFriendViewModel.getUserListByListId(list)
        listFriendViewModel.listFriend.observe(viewLifecycleOwner){
            when(it.status){
                Status.LOADING -> {
//                    showLoading()
                }
                Status.SUCCESS -> {
                    it.data?.let { list ->
                        val recyclerView = binding.friendRecyclerView
                        val adapter = ListFriendAdapter(list)
                        recyclerView.adapter = adapter
                        recyclerView.layoutManager = LinearLayoutManager(requireContext())
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

    }

}