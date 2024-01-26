package com.android.hoang.chatapplication.ui.main.friend.friendviewpager.alluser

import android.content.Intent
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
import com.android.hoang.chatapplication.databinding.FragmentAllUserBinding
import com.android.hoang.chatapplication.ui.auth.AuthActivity
import com.android.hoang.chatapplication.ui.main.MainActivity
import com.android.hoang.chatapplication.util.Constants.LOG_TAG
import com.android.hoang.chatapplication.util.Status
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AllUserFragment : BaseFragment<FragmentAllUserBinding>() {

    private val allUserViewModel: AllUserFragmentViewModel by viewModels()

    override fun prepareView(savedInstanceState: Bundle?) {

        initListUser()

    }

    private fun initListUser(){
//        val dataList = mutableListOf<Any>("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z")

        allUserViewModel.userList.observe(viewLifecycleOwner){
            when(it.status){
                Status.LOADING -> showLoading()
                Status.SUCCESS -> {
                    it.data?.let { list ->
                        Log.d(LOG_TAG, "allUserFragment.initListUser: ${list[0].id}")
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

    private fun sortRecyclerView(userList: List<UserFirebase>){
        val list = mutableListOf<Any>()
        val recyclerView = binding.recyclerView
        for (user: UserFirebase in userList){
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

        val adapter = AllUserAdapter(sortedDataList, allUserViewModel)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentAllUserBinding.inflate(inflater, container, false)

}