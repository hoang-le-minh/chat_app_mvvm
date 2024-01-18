package com.android.hoang.chatapplication.ui.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.hoang.chatapplication.R
import com.android.hoang.chatapplication.util.Constants.MESSAGE_TYPE_IMAGE
import com.android.hoang.chatapplication.util.Constants.MESSAGE_TYPE_STICKER
import com.android.hoang.chatapplication.util.Constants.MESSAGE_TYPE_STRING

class ChatAdapter(private val messages: ArrayList<Any>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.all_user_section_header, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return messages.size
    }
}