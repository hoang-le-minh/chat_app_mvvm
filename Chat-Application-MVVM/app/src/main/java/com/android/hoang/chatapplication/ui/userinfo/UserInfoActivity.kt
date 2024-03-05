package com.android.hoang.chatapplication.ui.userinfo

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.viewModels
import com.android.hoang.chatapplication.R
import com.android.hoang.chatapplication.base.BaseActivity
import com.android.hoang.chatapplication.databinding.ActivityUserInfoBinding
import com.android.hoang.chatapplication.ui.main.MainActivity
import com.android.hoang.chatapplication.ui.main.MainActivityViewModel
import com.android.hoang.chatapplication.util.Constants.LOG_TAG
import com.android.hoang.chatapplication.util.Status
import com.android.hoang.chatapplication.util.showMessage
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserInfoActivity : BaseActivity<ActivityUserInfoBinding>() {

    private val userInfoViewModel: UserInfoActivityViewModel by viewModels()

    override fun getActivityBinding(inflater: LayoutInflater) = ActivityUserInfoBinding.inflate(inflater)

    override fun prepareView(savedInstanceState: Bundle?) {
        observeModel()

        val username = intent.getStringExtra("username")
        binding.txtUsername.text = username.toString()

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.txtDeleteConversation.setOnClickListener {
            onClickDeleteConversation()
        }
    }

    private fun onClickDeleteConversation() {
        showMessage(R.string.question_delete_conversation){
            observeDelete()
        }
    }

    private fun observeDelete() {
        val userId = intent.getStringExtra("userId") ?: return
        userInfoViewModel.deleteConversation(userId)
        userInfoViewModel.resultDelete.observe(this){
            when(it.status){
                Status.LOADING -> { showLoading() }
                Status.SUCCESS -> {
                    Toast.makeText(this, getString(R.string.delete_successfully), Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    finishAffinity()
                    startActivity(intent)
                    hideLoading()
                }
                Status.ERROR -> {
                    it.message.let { msg ->
                        hideLoading()
                        Toast.makeText(this, msg.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun observeModel(){
        val userId = intent.getStringExtra("userId") ?: return

        userInfoViewModel.getUserInfoById(userId)
        userInfoViewModel.result.observe(this){
            when(it.status){
                Status.LOADING -> {  }
                Status.SUCCESS -> {
                    it.data?.let { user ->
                        Log.d(LOG_TAG, "observeModel: $user")
                        Glide.with(this).load(user.imageUrl).error(R.drawable.avt_default).into(binding.userAvt)
                        binding.txtDob.text = if (user.dateOfBirth != "") user.dateOfBirth else getString(R.string.unknown)
                        binding.txtPhoneNumber.text = if (user.phoneNumber != "") user.phoneNumber else getString(R.string.unknown)
                    }
//                    hideLoading()
                }
                Status.ERROR -> {
                    it.message.let { msg ->
//                        hideLoading()
                        Toast.makeText(this, msg.toString(), Toast.LENGTH_LONG).show()
                    }
                }
            }
        }


        userInfoViewModel.getRelationship(userId)
        userInfoViewModel.resultRelationship.observe(this){
            when(it.status){
                Status.LOADING -> {}
                Status.SUCCESS -> {
                    it.data?.let { result -> statusAddFriendButton(result, userId) }
                }
                Status.ERROR -> {
                    it.message.let { msg ->
                        hideLoading()
                        Toast.makeText(this, msg.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    // 1: la ban be || 2: da gui loi moi || 3: cho xac nhan || 0: nothing
    private fun statusAddFriendButton(result: Int, userId: String){
        when (result){
            1 -> {
                binding.txtAddOrCancelFriend.text = getString(R.string.unfriend)
                binding.txtAddOrCancelFriend.setOnClickListener {
                    userInfoViewModel.unfriend(userId)
                    userInfoViewModel.getRelationship(userId)
                }
            }
            2 -> {
                binding.txtAddOrCancelFriend.text = getString(R.string.cancel_friend_request)
                binding.txtAddOrCancelFriend.setOnClickListener {
                    userInfoViewModel.cancelFriendRequest(userId)
                    userInfoViewModel.getRelationship(userId)

                }
            }
            3 -> {
                binding.txtAddOrCancelFriend.text = getString(R.string.accept_friend)
                binding.txtAddOrCancelFriend.setOnClickListener {
                    userInfoViewModel.acceptFriend(userId)
                    userInfoViewModel.getRelationship(userId)

                }
            }
            0 -> {
                binding.txtAddOrCancelFriend.text = getString(R.string.add_friend)
                binding.txtAddOrCancelFriend.setOnClickListener {
                    userInfoViewModel.addFriend(userId)
                    userInfoViewModel.getRelationship(userId)

                }
            }
        }
    }
}