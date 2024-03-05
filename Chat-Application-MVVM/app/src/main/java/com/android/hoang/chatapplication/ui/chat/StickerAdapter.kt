package com.android.hoang.chatapplication.ui.chat

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.hoang.chatapplication.R
import com.android.hoang.chatapplication.data.remote.model.UserFirebase
import com.android.hoang.chatapplication.ui.main.friend.friendviewpager.alluser.AllUserAdapter
import com.android.hoang.chatapplication.util.Constants.LOG_TAG
import com.android.hoang.chatapplication.util.Constants.MESSAGE_TYPE_IMAGE
import com.android.hoang.chatapplication.util.Constants.MESSAGE_TYPE_STICKER
import com.android.hoang.chatapplication.util.Status
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth

class StickerAdapter(private val list: List<Any>, private val chatViewModel: ChatActivityViewModel, private val lifecycleOwner: LifecycleOwner, private val userId: String) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_IMAGE = 1
    private val VIEW_TYPE_STICKER = 2

    inner class StickerViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private val stickerItem: ImageView = itemView.findViewById(R.id.sticker_item)
        fun onBind(resource: Int){

            stickerItem.setImageResource(resource)
            stickerItem.setOnClickListener {
                Log.d(LOG_TAG, "stickerAdapter.onBind: send $resource")
                val currentUser = FirebaseAuth.getInstance().currentUser
                if (currentUser != null){
                    sendMessage(itemView.context, currentUser.uid, userId, adapterPosition.toString(), MESSAGE_TYPE_STICKER)
                }
            }
        }
    }

    inner class ImageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private val photoItem: ImageView = itemView.findViewById(R.id.sticker_item)
        fun onBind(path: String){

            Glide.with(itemView.context).load(path).error(R.drawable.no_image).into(photoItem)
            photoItem.setOnClickListener {
                Log.d(LOG_TAG, "stickerAdapter.onBind: send $path")
                val currentUser = FirebaseAuth.getInstance().currentUser
                if (currentUser != null){
                    sendMessage(itemView.context, currentUser.uid, userId, path, MESSAGE_TYPE_IMAGE)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_STICKER -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.custom_sticker_item, parent, false)
                StickerViewHolder(view)
            }
            VIEW_TYPE_IMAGE -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.custom_sticker_item, parent, false)
                ImageViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is StickerAdapter.StickerViewHolder -> {
                val sticker = list[position] as Int
                holder.onBind(sticker)
            }
            is StickerAdapter.ImageViewHolder -> {
                val photoPath = list[position] as String
                holder.onBind(photoPath)
            }
        }
    }

    private fun sendMessage(context: Context, senderId: String, receiverId: String, message: String, type: Int){
        if (message.isEmpty()) return
        chatViewModel.sendMessage(senderId, receiverId, message, type)
        chatViewModel.result.observe(lifecycleOwner){
            when(it.status){
                Status.LOADING -> { (context as ChatActivity).showLoading() }
                Status.SUCCESS -> {
                    chatViewModel.readMessage(senderId, receiverId)
                    chatViewModel.checkConversationCreated(senderId, receiverId)
                    (context as ChatActivity).hideLoading()
                }
                Status.ERROR -> {
                    it.message.let { msg ->
                        Toast.makeText(context, msg.toString(), Toast.LENGTH_LONG).show()
                    }
                    (context as ChatActivity).hideLoading()
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (list[position] is String) {
            VIEW_TYPE_IMAGE
        } else {
            VIEW_TYPE_STICKER
        }
    }

}