package com.android.hoang.chatapplication.ui.chat

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.hoang.chatapplication.R
import com.android.hoang.chatapplication.base.BaseActivity
import com.android.hoang.chatapplication.data.remote.model.Conversation
import com.android.hoang.chatapplication.data.remote.model.Message
import com.android.hoang.chatapplication.databinding.ActivityChatBinding
import com.android.hoang.chatapplication.util.Constants
import com.android.hoang.chatapplication.util.Constants.LOG_TAG
import com.android.hoang.chatapplication.util.Constants.MESSAGE_TYPE_STRING
import com.android.hoang.chatapplication.util.Status
import com.blankj.utilcode.util.StringUtils
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap
import kotlin.coroutines.resume

@AndroidEntryPoint
class ChatActivity : BaseActivity<ActivityChatBinding>() {

    private val chatViewModel: ChatActivityViewModel by viewModels()
    private lateinit var chatAdapter: ChatAdapter

    override fun getActivityBinding(inflater: LayoutInflater) = ActivityChatBinding.inflate(layoutInflater)

    override fun prepareView(savedInstanceState: Bundle?) {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window.statusBarColor = resources.getColor(R.color.black);
        val userId = intent.getStringExtra("user_id") ?: return
        val userAvt = intent.getStringExtra("user_avt") ?: return
        loadUserInfo()
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return
        readMessage(currentUser.uid, userId, userAvt)

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnSendMessage.setOnClickListener {
            val message = binding.edtTypingMessage.text.toString()

            val fCurrentUser = FirebaseAuth.getInstance().currentUser ?: return@setOnClickListener
            sendMessage(fCurrentUser.uid, userId, message, MESSAGE_TYPE_STRING)

        }

        chatViewModel.seenMessage(currentUser.uid, userId)

    }

    private fun readMessage(senderId: String, receiverId: String, profileImage: String){
        chatViewModel.readMessage(senderId, receiverId)
        chatViewModel.messageList.observe(this){
            when(it.status){
                Status.LOADING -> {}
                Status.SUCCESS -> {
                    hideLoading()
                    it.data?.let { list ->
                        val recyclerView = binding.recyclerViewChat
                        chatAdapter = ChatAdapter(list, profileImage)
                        val linearLayoutManager = LinearLayoutManager(this)
                        linearLayoutManager.stackFromEnd = true
                        linearLayoutManager.scrollToPosition(chatAdapter.itemCount - 1)
                        recyclerView.layoutManager = linearLayoutManager
                        recyclerView.adapter = chatAdapter
                    }
                }
                Status.ERROR -> {
                    it.message.let { msg ->
                        hideLoading()
                    }
                }
            }
        }
    }

    private fun loadUserInfo(){
        val username = intent.getStringExtra("username")
        val userAvt = intent.getStringExtra("user_avt")
        Log.d(LOG_TAG, "prepareView: $username")
        val errorAvt = if(userAvt == "") R.drawable.avt_default else R.drawable.no_image

        binding.txtUsername.text = username
        Glide.with(this).load(userAvt).error(errorAvt).into(binding.userAvt)
    }

    private fun sendMessage(senderId: String, receiverId: String, message: String, type: Int){
        if (message.isEmpty()) return
        chatViewModel.sendMessage(senderId, receiverId, message, type)
        chatViewModel.result.observe(this){
            when(it.status){
                Status.LOADING -> {}
                Status.SUCCESS -> {
                    hideLoading()
                    binding.edtTypingMessage.setText("")
                    chatViewModel.readMessage(senderId, receiverId)
                    chatViewModel.checkConversationCreated(senderId, receiverId)
                }
                Status.ERROR -> {
                    it.message.let { msg ->
                        hideLoading()
                        Toast.makeText(this, msg.toString(), Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        chatViewModel.removeSeenListener()
    }

}