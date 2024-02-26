package com.android.hoang.chatapplication.ui.main.home.createconversation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.hoang.chatapplication.R
import com.android.hoang.chatapplication.base.BaseFragment
import com.android.hoang.chatapplication.data.remote.model.ListString
import com.android.hoang.chatapplication.data.remote.model.UserFirebase
import com.android.hoang.chatapplication.databinding.FragmentCreateConversationBinding
import com.android.hoang.chatapplication.ui.chat.ChatActivity
import com.android.hoang.chatapplication.ui.main.friend.SearchUserAdapter
import com.android.hoang.chatapplication.util.Constants.LOG_TAG
import com.android.hoang.chatapplication.util.Status
import com.blankj.utilcode.util.LogUtils
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateConversationFragment : BaseFragment<FragmentCreateConversationBinding>() {

    private val createConversationViewModel: CreateConversationFragmentViewModel by viewModels()
    private lateinit var createConversationAdapter: CreateConversationAdapter

    private val args: CreateConversationFragmentArgs by navArgs()

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentCreateConversationBinding.inflate(inflater, container, false)

    override fun prepareView(savedInstanceState: Bundle?) {
        createConversationAdapter = CreateConversationAdapter(createConversationViewModel)
        observeModel()

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnCancelCreateNewMessage.setOnClickListener {
            findNavController().popBackStack()
        }

    }

    private fun observeModel(){
        val myListString = args.listUserId
        val userListId = myListString.list
//        Log.d(LOG_TAG, "observeModel: $userListId")
        createConversationViewModel.getUserListNotYetChat(userListId)
        createConversationViewModel.userListNotYetChat.observe(viewLifecycleOwner){
            when (it.status) {
                Status.SUCCESS -> {
                    it.data?.let { listUser ->
                        binding.recyclerviewFriendList.adapter = createConversationAdapter
                        binding.recyclerviewFriendList.layoutManager = LinearLayoutManager(requireContext())
                        createConversationAdapter.submitList(listUser)
                        searchUser(listUser)
                    }
                    hideLoading()
                }
                Status.LOADING -> {
                    LogUtils.d("$this LOADING")
                    showLoading()
                }
                Status.ERROR -> {
                    LogUtils.d("$this ERROR")
                    hideLoading()
                }
            }
        }

        createConversationViewModel.userSelected.observe(viewLifecycleOwner){ user ->
            if (user != null){
                binding.bottomLayout.visibility = View.VISIBLE
                binding.txtUsername.text = user.username
                Glide.with(requireContext()).load(user.imageUrl).error(R.drawable.avt_default).into(binding.userAvtChecked)
                binding.btnCreateNewMessage.setOnClickListener {
                    val intent = Intent(requireContext(), ChatActivity::class.java)
                    intent.putExtra("user_id", user.id)
                    findNavController().popBackStack()
                    startActivity(intent)
                }
            } else{
                binding.bottomLayout.visibility = View.GONE
            }
        }
    }

    private fun searchUser(list: List<UserFirebase>) {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText, list)

                return true
            }

        })
    }

    private fun filterList(s: String?, list: List<UserFirebase>){
        if (s != null) {
            val currentUser = FirebaseAuth.getInstance().currentUser ?: return
            val userList = mutableListOf<UserFirebase>()
            for (user: UserFirebase in list) {
                if (user.id != currentUser.uid && user.username.lowercase().contains(s.lowercase())) {
                    userList.add(user)
//                                Log.d(LOG_TAG, "searchUser.onDataChange: ${user.username}")
                }
            }
            Log.d(LOG_TAG, "createConversation.searchUser: $userList")
            createConversationAdapter.submitList(userList)
            if(userList.isEmpty()){
                binding.noMatchingResult.visibility = View.VISIBLE
            } else {
                binding.noMatchingResult.visibility = View.GONE
            }
        }
    }


}