package com.android.hoang.chatapplication.ui.main.friend.friendviewpager.listfriend

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.hoang.chatapplication.R
import com.android.hoang.chatapplication.base.BaseFragment
import com.android.hoang.chatapplication.data.remote.model.UserFirebase
import com.android.hoang.chatapplication.databinding.FragmentListFriendBinding
import com.android.hoang.chatapplication.ui.main.friend.friendviewpager.alluser.AllUserAdapter
import com.android.hoang.chatapplication.util.Constants
import com.android.hoang.chatapplication.util.Status
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListFriendFragment : BaseFragment<FragmentListFriendBinding>() {

    private val listFriendViewModel: ListFriendFragmentViewModel by viewModels()

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
                        Log.d(Constants.LOG_TAG, "allUserFragment.initListUser: ${list[0]}")

                        initListFriend(list)
                    }
//                    hideLoading()
                }
                Status.ERROR -> {
                    it.message.let { msg ->
                        Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
                        hideLoading()
                    }
                }
            }
        }
    }

    private fun initListFriend(list: List<String>){
        listFriendViewModel.getUserListByListId(list)
        listFriendViewModel.listFriend.observe(viewLifecycleOwner){
            when(it.status){
                Status.LOADING -> showLoading()
                Status.SUCCESS -> {
                    it.data?.let { list ->
                        Log.d(Constants.LOG_TAG, "listFriendFragment.initListFriend: ${list[0]}")

                        sortRecyclerView(list)
                    }
                    hideLoading()
                }
                Status.ERROR -> {
                    it.message.let { msg ->
                        Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
                        hideLoading()
                    }
                }
            }
        }

    }

    private fun sortRecyclerView(friendList: List<UserFirebase>){
        val list = mutableListOf<Any>()
        val recyclerView = binding.friendRecyclerView
        for (user: UserFirebase in friendList){
//            Log.d(LOG_TAG, "initListUser: for ${user.username}")
            list.add(user)
            val ch = user.username.split(" ").last().substring(0,1).uppercase()
            if(list.none{ it is String && it == ch }){
                list.add(ch)
            }
        }

        val sortedDataList = list.sortedBy {
            if (it is String) it
            else (it as UserFirebase).username.split(" ").last()
        }

        val adapter = ListFriendAdapter(sortedDataList)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }
}