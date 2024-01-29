package com.android.hoang.chatapplication.ui.main.friend.friendviewpager.friendrequest

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
import com.android.hoang.chatapplication.R
import com.android.hoang.chatapplication.base.BaseFragment
import com.android.hoang.chatapplication.data.remote.model.UserFirebase
import com.android.hoang.chatapplication.databinding.FragmentFriendRequestBinding
import com.android.hoang.chatapplication.ui.main.MainActivityViewModel
import com.android.hoang.chatapplication.ui.main.friend.friendviewpager.listfriend.ListFriendAdapter
import com.android.hoang.chatapplication.util.Constants
import com.android.hoang.chatapplication.util.Status
import com.blankj.utilcode.util.StringUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FriendRequestFragment : BaseFragment<FragmentFriendRequestBinding>() {

    private val friendRequestViewModel: FriendRequestFragmentViewModel by viewModels()
    private val mainViewModel: MainActivityViewModel by activityViewModels()

    override fun prepareView(savedInstanceState: Bundle?) {
        observerModel()
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentFriendRequestBinding.inflate(inflater, container, false)

    private fun observerModel(){
        // request list
        friendRequestViewModel.getRequestIdList()
        friendRequestViewModel.listIdRequest.observe(viewLifecycleOwner){
            when(it.status){
                Status.LOADING -> {
//                    showLoading()
                }
                Status.SUCCESS -> {
                    it.data?.let { list ->
//                        Log.d(Constants.LOG_TAG, "friendRequest.initListRequestUser: ${list[0]}")
                        initListRequest(list)
                        mainViewModel.setRequestCount(list.size)
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

        // sent list
        friendRequestViewModel.getSentIdList()
        friendRequestViewModel.listIdSent.observe(viewLifecycleOwner){
            when(it.status){
                Status.LOADING -> {
//                    showLoading()
                }
                Status.SUCCESS -> {
                    it.data?.let { list ->

                        initListSent(list)
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

        mainViewModel.updateRequestStatus.observe(viewLifecycleOwner){
            if (it){
                friendRequestViewModel.getRequestIdList()
                friendRequestViewModel.getSentIdList()
                mainViewModel.updateRequestStatus(false)
            }
        }

    }

    private fun initListRequest(list: MutableList<String>){
        friendRequestViewModel.getRequestListByListId(list)
        friendRequestViewModel.listRequest.observe(viewLifecycleOwner){
            when(it.status){
                Status.LOADING -> {
//                    showLoading()
                }
                Status.SUCCESS -> {
                    it.data?.let { list ->
//                        Log.d(Constants.LOG_TAG, "friendRequest.initListRequest: ${list[0]}")

                        initRequestRecyclerView(list)
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

    private fun initRequestRecyclerView(requestList: MutableList<UserFirebase>){
        val recyclerView = binding.requestRecyclerView

        val adapter = FriendRequestAdapter(mainViewModel, friendRequestViewModel, true)
        friendRequestViewModel.resultAccept.observe(viewLifecycleOwner){
//            if (it.status == Status.SUCCESS){
//                adapter.setOnButtonClickListener(object : FriendRequestAdapter.OnButtonClickListener{
//                    override fun onButtonClick(position: Int) {
//                        requestList.removeAt(position)
//                        adapter.notifyItemRemoved(position)
//                    }
//
//                })
//                adapter.submitList(requestList)
//            }
        }
        adapter.submitList(requestList)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun initListSent(list: MutableList<String>){
        friendRequestViewModel.getSentListByListId(list)
        friendRequestViewModel.listSent.observe(viewLifecycleOwner){
            when(it.status){
                Status.LOADING -> {
//                    showLoading()
                }
                Status.SUCCESS -> {
                    it.data?.let { list ->
//                        Log.d(Constants.LOG_TAG, "friendRequest.initListSent: ${list[0]}")

                        initSentRecyclerView(list)
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

    private fun initSentRecyclerView(sentList: MutableList<UserFirebase>){
        val recyclerView = binding.sentRecyclerView

        val adapter = FriendRequestAdapter(mainViewModel, friendRequestViewModel, false)
        friendRequestViewModel.resultCancelRequest.observe(viewLifecycleOwner){
//            if (it.status == Status.SUCCESS){
//                adapter.setOnButtonClickListener(object : FriendRequestAdapter.OnButtonClickListener{
//                    override fun onButtonClick(position: Int) {
//                        sentList.removeAt(position)
//                        adapter.notifyItemRemoved(position)
//                    }
//
//                })
//                adapter.submitList(sentList)
//            }
        }
        adapter.submitList(sentList)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

}