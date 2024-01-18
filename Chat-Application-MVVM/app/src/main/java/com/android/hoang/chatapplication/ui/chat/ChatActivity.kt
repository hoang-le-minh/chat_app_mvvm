package com.android.hoang.chatapplication.ui.chat

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.android.hoang.chatapplication.R
import com.android.hoang.chatapplication.base.BaseActivity
import com.android.hoang.chatapplication.databinding.ActivityChatBinding
import com.android.hoang.chatapplication.util.Constants.LOG_TAG
import com.android.hoang.chatapplication.util.Constants.MESSAGE_TYPE_STRING
import com.android.hoang.chatapplication.util.Status
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatActivity : BaseActivity<ActivityChatBinding>() {

    private val chatViewModel: ChatActivityViewModel by viewModels()

    override fun getActivityBinding(inflater: LayoutInflater) = ActivityChatBinding.inflate(layoutInflater)

    override fun prepareView(savedInstanceState: Bundle?) {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window.statusBarColor = resources.getColor(R.color.black);
        val userId = intent.getStringExtra("user_id") ?: return
        loadUserInfo()

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnSendMessage.setOnClickListener {
            val message = binding.edtTypingMessage.text.toString()
            if (message.isEmpty()){
                Toast.makeText(this, "Message is empty", Toast.LENGTH_LONG).show()
            } else {
                val fCurrentUser = FirebaseAuth.getInstance().currentUser ?: return@setOnClickListener
                sendMessage(fCurrentUser.uid, userId, message, MESSAGE_TYPE_STRING)
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
        chatViewModel.sendMessage(senderId, receiverId, message, type)
        chatViewModel.result.observe(this){
            when(it.status){
                Status.LOADING -> showLoading()
                Status.SUCCESS -> {
                    hideLoading()
                    binding.edtTypingMessage.setText("")
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

}