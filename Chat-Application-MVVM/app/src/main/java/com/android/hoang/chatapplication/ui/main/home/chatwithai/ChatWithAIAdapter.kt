package com.android.hoang.chatapplication.ui.main.home.chatwithai

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.hoang.chatapplication.R
import com.android.hoang.chatapplication.data.remote.model.Message
import com.android.hoang.chatapplication.ui.chat.ChatAdapter
import com.android.hoang.chatapplication.ui.chat.ViewClickListener
import com.android.hoang.chatapplication.util.Constants
import com.android.hoang.chatapplication.util.listStickerResource
import com.blankj.utilcode.util.StringUtils
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.text.SimpleDateFormat
import java.util.*

class ChatWithAIAdapter(private val messages: List<Any>) : RecyclerView.Adapter<ChatWithAIAdapter.MessageViewHolder>() {

    private val MESSAGE_LEFT = 0
    private val MESSAGE_RIGHT = 1

    private var currentUser: FirebaseUser? = null

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtMessage = itemView.findViewById<TextView>(R.id.txt_message_chat)
        private val imageMessage = itemView.findViewById<ImageView>(R.id.image_message_chat)
        private val profileImage = itemView.findViewById<ImageView>(R.id.profile_image_chat)
        private val txtTimestamp = itemView.findViewById<TextView>(R.id.txt_timestamp_chat)
        private val txtSeen = itemView.findViewById<TextView>(R.id.txt_seen)

        fun onBind(message: Message){
            profileImage.setImageResource(R.drawable.chatbot)

            if (message.type == Constants.MESSAGE_TYPE_STRING.toString()){
                txtMessage.visibility = View.VISIBLE
                imageMessage.visibility = View.GONE
                txtMessage.text = message.message
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
                Log.d(Constants.LOG_TAG, "onBind: $message")
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