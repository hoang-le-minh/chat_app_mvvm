package com.android.hoang.chatapplication.ui.chat

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnItemTouchListener
import com.android.hoang.chatapplication.R
import com.android.hoang.chatapplication.base.BaseActivity
import com.android.hoang.chatapplication.data.remote.model.Message
import com.android.hoang.chatapplication.data.remote.model.NotificationData
import com.android.hoang.chatapplication.data.remote.model.PushNotification
import com.android.hoang.chatapplication.data.remote.model.UserFirebase
import com.android.hoang.chatapplication.databinding.ActivityChatBinding
import com.android.hoang.chatapplication.ui.userinfo.UserInfoActivity
import com.android.hoang.chatapplication.util.Constants.LOG_TAG
import com.android.hoang.chatapplication.util.Constants.MESSAGE_TYPE_STRING
import com.android.hoang.chatapplication.util.Status
import com.android.hoang.chatapplication.util.listStickerResource
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

@AndroidEntryPoint
class ChatActivity : BaseActivity<ActivityChatBinding>() {

    private val chatViewModel: ChatActivityViewModel by viewModels()
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var stickerAdapter: StickerAdapter
    private lateinit var photoAdapter: StickerAdapter

    private val MY_READ_PERMISSION_CODE = 1001
    var topic = ""
    var username = ""

    private var isStickerShow = false
    private var isPhotoViewShow = false

    override fun getActivityBinding(inflater: LayoutInflater) = ActivityChatBinding.inflate(layoutInflater)

    override fun prepareView(savedInstanceState: Bundle?) {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)

        val userId = intent.getStringExtra("user_id") ?: return

        loadUserInfo()

        binding.stickerIcon.setOnClickListener {
            onClickStickerIcon()
        }

        binding.btnAddPhoto.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    MY_READ_PERMISSION_CODE
                )
            } else{
                onClickPhotoView()
            }
        }

        binding.layoutChat.setOnClickListener {
            if (isStickerShow){
                hideStickerView()
            }
        }

        binding.btnBack.setOnClickListener {
            val intent = Intent("com.android.hoang.chatapplication.UPDATE_HOME_UI")
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
            finish()
        }
        // Set up callback for the back button press
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (isStickerShow){
                    hideStickerView()
                } else if(isPhotoViewShow){
                    hidePhotoView()
                } else {
                    val intent = Intent("com.android.hoang.chatapplication.UPDATE_HOME_UI")
                    LocalBroadcastManager.getInstance(this@ChatActivity).sendBroadcast(intent)
                    finish()
                }
            }
        }
        // Add the callback to the OnBackPressedDispatcher
        onBackPressedDispatcher.addCallback(this, callback)

        val currentUser = FirebaseAuth.getInstance().currentUser ?: return
        binding.btnSendMessage.setOnClickListener {
            val message = binding.edtTypingMessage.text.toString()

            val fCurrentUser = FirebaseAuth.getInstance().currentUser ?: return@setOnClickListener
            sendMessage(fCurrentUser.uid, userId, message, MESSAGE_TYPE_STRING)
            topic = "/topics/$userId"
            PushNotification(NotificationData(username, message), topic).also {
                chatViewModel.sendNotification(it)
            }
        }

        chatViewModel.seenMessage(currentUser.uid, userId)

        binding.userAvt.setOnClickListener{
            val intent = Intent(this, UserInfoActivity::class.java)
            intent.putExtra("userId", userId)
            intent.putExtra("username", username)
            startActivity(intent)
        }

        binding.txtUsername.setOnClickListener {
            val intent = Intent(this, UserInfoActivity::class.java)
            intent.putExtra("userId", userId)
            intent.putExtra("username", username)
            startActivity(intent)
        }

    }

    private fun onClickStickerIcon(){
        val userId = intent.getStringExtra("user_id") ?: return
        if (isStickerShow){
            hideStickerView()
        } else {
            showStickerView()
            val stickerRecyclerView = binding.stickerRecyclerView
            val layoutManager = GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false)
            stickerAdapter = StickerAdapter(listStickerResource(), chatViewModel, this@ChatActivity, userId)

            stickerRecyclerView.layoutManager = layoutManager
            stickerRecyclerView.adapter = stickerAdapter
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == MY_READ_PERMISSION_CODE){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                onClickPhotoView()
            } else{
                Toast.makeText(this, StringUtils.getString(R.string.permission_not_granted), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onClickPhotoView(){
        val userId = intent.getStringExtra("user_id") ?: return
        if (isPhotoViewShow) {
            hidePhotoView()
        } else {
            showPhotoView()
            val stickerRecyclerView = binding.stickerRecyclerView
            val layoutManager = GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false)
            val images = ImagesGallery.listOfImages(this) as MutableList<String>
            photoAdapter = StickerAdapter(images, chatViewModel, this@ChatActivity, userId)
            stickerRecyclerView.layoutManager = layoutManager
            stickerRecyclerView.adapter = photoAdapter
        }

    }

    private fun showPhotoView(){
        binding.btnAddPhoto.setImageResource(R.drawable.ic_add_photo_selected)
        binding.stickerIcon.setImageResource(R.drawable.ic_smile)
        val layoutParams = binding.stickerRecyclerView.layoutParams
        layoutParams.height = 600
        binding.stickerRecyclerView.layoutParams = layoutParams
        binding.stickerRecyclerView.visibility = View.VISIBLE
        isPhotoViewShow = true
        isStickerShow = false
    }

    private fun hidePhotoView(){
        binding.btnAddPhoto.setImageResource(R.drawable.ic_add_photo)
        val layoutParams = binding.stickerRecyclerView.layoutParams
        layoutParams.height = 0
        binding.stickerRecyclerView.layoutParams = layoutParams
        binding.stickerRecyclerView.visibility = View.GONE
        isPhotoViewShow = false
    }

    private fun showStickerView(){
        binding.stickerIcon.setImageResource(R.drawable.ic_smile_selected)
        binding.btnAddPhoto.setImageResource(R.drawable.ic_add_photo)
        val layoutParams = binding.stickerRecyclerView.layoutParams
        layoutParams.height = 600
        binding.stickerRecyclerView.layoutParams = layoutParams
        binding.stickerRecyclerView.visibility = View.VISIBLE
        isStickerShow = true
        isPhotoViewShow = false
    }

    private fun hideStickerView(){
        binding.stickerIcon.setImageResource(R.drawable.ic_smile)
        val layoutParams = binding.stickerRecyclerView.layoutParams
        layoutParams.height = 0
        binding.stickerRecyclerView.layoutParams = layoutParams
        binding.stickerRecyclerView.visibility = View.INVISIBLE
        isStickerShow = false
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
                        chatAdapter = ChatAdapter(list, profileImage, object : ViewClickListener{
                            override fun onViewClick() {
                                if (isStickerShow){
                                    hideStickerView()
                                }
                            }

                        })
                        val linearLayoutManager = LinearLayoutManager(this)
                        linearLayoutManager.stackFromEnd = true
                        linearLayoutManager.scrollToPosition(chatAdapter.itemCount - 1)
                        recyclerView.layoutManager = linearLayoutManager
                        recyclerView.adapter = chatAdapter

                        onRecyclerViewScrollListener(recyclerView, linearLayoutManager, list)
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

    private fun onRecyclerViewScrollListener(recyclerView: RecyclerView, layoutManager: LinearLayoutManager, list: MutableList<Message>){
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                val message = if (lastVisibleItemPosition < 0) list[0] else list[lastVisibleItemPosition]
                val createAtDate = SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault()).parse(message.createAt) ?: return
                val currentDate = Calendar.getInstance().time
                when{
                    isSameDay(createAtDate, currentDate) -> binding.txtDate.text = StringUtils.getString(R.string.today)
                    isYesterday(createAtDate, currentDate) -> binding.txtDate.text = StringUtils.getString(R.string.yesterday)
                    else -> binding.txtDate.text = message.createAt.split(" ").last()
                }
            }
        })
    }

    private fun isSameDay(date1: Date, date2: Date): Boolean {
        val cal1 = Calendar.getInstance().apply { time = date1 }
        val cal2 = Calendar.getInstance().apply { time = date2 }

        return cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
    }

    private fun isYesterday(date1: Date, date2: Date): Boolean {
        val cal1 = Calendar.getInstance().apply { time = date1 }
        val cal2 = Calendar.getInstance().apply { time = date2 }
        cal2.add(Calendar.DAY_OF_YEAR, -1) // Giảm 1 ngày để kiểm tra hôm qua

        return cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
    }

    private fun loadUserInfo(){

        val userId = intent.getStringExtra("user_id") ?: return
        val myRef = FirebaseDatabase.getInstance().getReference("users").child(userId)
        myRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(UserFirebase::class.java)
                Log.d(LOG_TAG, "prepareView: ${user?.username}")
                val errorAvt = if(user?.imageUrl == "") R.drawable.avt_default else R.drawable.no_image
                username = user?.username.toString()
                binding.txtUsername.text = user?.username
                Glide.with(this@ChatActivity).load(user?.imageUrl).error(errorAvt).into(binding.userAvt)

                val currentUser = FirebaseAuth.getInstance().currentUser ?: return
                readMessage(currentUser.uid, userId, user?.imageUrl ?: "")
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
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

//    fun listStickerResource(): List<Int> {
//        return listOf(
//            R.drawable.sticker_1,
//            R.drawable.sticker_2,
//            R.drawable.sticker_3,
//            R.drawable.sticker_4,
//            R.drawable.sticker_5,
//            R.drawable.sticker_6,
//            R.drawable.sticker_7,
//            R.drawable.sticker_8,
//            R.drawable.sticker_9,
//            R.drawable.sticker_10,
//            R.drawable.sticker_11,
//            R.drawable.sticker_12,
//        )
//    }
}