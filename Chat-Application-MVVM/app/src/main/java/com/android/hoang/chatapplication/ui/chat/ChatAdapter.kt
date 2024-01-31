package com.android.hoang.chatapplication.ui.chat

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.hoang.chatapplication.R
import com.android.hoang.chatapplication.data.remote.model.Message
import com.android.hoang.chatapplication.util.Constants.LOG_TAG
import com.android.hoang.chatapplication.util.Constants.MESSAGE_TYPE_IMAGE
import com.android.hoang.chatapplication.util.Constants.MESSAGE_TYPE_STICKER
import com.android.hoang.chatapplication.util.Constants.MESSAGE_TYPE_STRING
import com.blankj.utilcode.util.StringUtils
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

interface ViewClickListener {
    fun onViewClick()
}

class ChatAdapter(private val messages: List<Any>, private val receiverProfileUrl: String, private val itemClickListener: ViewClickListener) : RecyclerView.Adapter<ChatAdapter.MessageViewHolder>() {

    private val MESSAGE_LEFT = 0
    private val MESSAGE_RIGHT = 1

    private var currentUser: FirebaseUser? = null

    private fun onViewClick() {
        itemClickListener.onViewClick()
    }

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtMessage = itemView.findViewById<TextView>(R.id.txt_message_chat)
        private val imageMessage = itemView.findViewById<ImageView>(R.id.image_message_chat)
        private val profileImage = itemView.findViewById<ImageView>(R.id.profile_image_chat)
        private val txtTimestamp = itemView.findViewById<TextView>(R.id.txt_timestamp_chat)
        private val txtSeen = itemView.findViewById<TextView>(R.id.txt_seen)

        fun onBind(message: Message){
            val errorUrl = if (receiverProfileUrl == "") R.drawable.avt_default else R.drawable.no_image
            Glide.with(itemView.context).load(receiverProfileUrl).error(errorUrl).into(profileImage)

            itemView.setOnClickListener {
                onViewClick()
            }

            if (message.type == MESSAGE_TYPE_STRING.toString()){
                txtMessage.visibility = View.VISIBLE
                imageMessage.visibility = View.GONE
                txtMessage.text = message.message
            } else if (message.type == MESSAGE_TYPE_STICKER.toString()){
                txtMessage.visibility = View.GONE
                imageMessage.visibility = View.VISIBLE
                imageMessage.setImageResource(message.message.toInt())
            } else {
                txtMessage.visibility = View.GONE
                imageMessage.visibility = View.VISIBLE
                Glide.with(itemView.context).load(message.message).error(R.drawable.no_image).into(imageMessage)
            }
            // format date
            val sdf = SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault())
            val strCreateAt = message.createAt
            val createAtDate = sdf.parse(strCreateAt)

            val currentDate = Calendar.getInstance()
            currentDate.set(Calendar.HOUR_OF_DAY, 0)
            currentDate.set(Calendar.MINUTE, 0)
            currentDate.set(Calendar.SECOND, 0)
            currentDate.set(Calendar.MILLISECOND, 0)

            if (createAtDate?.before(currentDate.time) == false){
                txtTimestamp.text = strCreateAt.split(" ").first()
            } else {
                txtTimestamp.text = strCreateAt
            }

            if (adapterPosition == messages.size - 1){
                Log.d(LOG_TAG, "onBind: $message")
                if (message.isSeen.toBoolean()){
                    txtSeen.text = StringUtils.getString(R.string.is_seen)
                } else {
                    txtSeen.text = StringUtils.getString(R.string.is_delivered)
                }
            } else {
                txtSeen.visibility = View.GONE
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return when(viewType){
            MESSAGE_RIGHT -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.custom_sender_message, parent, false)
                MessageViewHolder(view)
            }
            MESSAGE_LEFT -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.custom_receiver_message, parent, false)
                MessageViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position] as Message
        holder.onBind(message)
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun getItemViewType(position: Int): Int {
        currentUser = FirebaseAuth.getInstance().currentUser
        return if ((messages[position] as Message).senderId == currentUser?.uid){
            MESSAGE_RIGHT
        } else MESSAGE_LEFT
    }
}