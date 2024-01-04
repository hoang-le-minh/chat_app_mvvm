package com.android.hoang.chatapplication.ui.main.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.hoang.chatapplication.R

class ListUserMessageAdapter : ListAdapter<UserTest, ListUserMessageAdapter.MyViewHolder>(
    AsyncDifferConfig.Builder(UserDiffCallBack()).build()) {


    inner class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var userAvt = itemView.findViewById<ImageView>(R.id.user_avt)
        var userName = itemView.findViewById<TextView>(R.id.user_name)
        var messageTime = itemView.findViewById<TextView>(R.id.message_time)
        var latestMessage = itemView.findViewById<TextView>(R.id.latest_message)
        var countMessageUnread = itemView.findViewById<TextView>(R.id.count_message_unread)
        fun onBind(user: UserTest){
            userAvt.setImageResource(user.imageResource)
            userName.text = user.name
            messageTime.text = user.latestTime
            latestMessage.text = user.latestMessage
            countMessageUnread.text = user.unreadCount.toString()
            if(user.unreadLatest){
                latestMessage.setTextColor(itemView.context.getColor(R.color.black))
                messageTime.setTextColor(itemView.context.getColor(R.color.black))
                countMessageUnread.visibility = View.VISIBLE
            } else {
                latestMessage.setTextColor(itemView.context.getColor(R.color.gray))
                messageTime.setTextColor(itemView.context.getColor(R.color.gray))
                countMessageUnread.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.custom_message_item_view, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }

}

class UserDiffCallBack: DiffUtil.ItemCallback<UserTest>(){
    override fun areItemsTheSame(oldItem: UserTest, newItem: UserTest): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: UserTest, newItem: UserTest): Boolean {
        return oldItem == newItem
    }

}