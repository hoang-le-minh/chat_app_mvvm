package com.yilmazgokhan.basestructure.ui.home

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yilmazgokhan.basestructure.R
import kotlinx.android.synthetic.main.custom_message_item_view.view.*

class ListUserMessageAdapter(): ListAdapter<UserTest, ListUserMessageAdapter.MyViewHolder>(AsyncDifferConfig.Builder(UserDiffCallBack()).build()) {


    inner class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun onBind(user: UserTest){
            itemView.user_avt.setImageResource(user.imageResource)
            itemView.user_name.text = user.name
            itemView.message_time.text = user.latestTime
            itemView.latest_message.text = user.latestMessage
            itemView.count_message_unread.text = user.unreadCount.toString()
            if(user.unreadLatest){
                itemView.latest_message.setTextColor(itemView.context.getColor(R.color.black))
                itemView.message_time.setTextColor(itemView.context.getColor(R.color.black))
                itemView.count_message_unread.visibility = View.VISIBLE
            } else {
                itemView.latest_message.setTextColor(itemView.context.getColor(R.color.gray))
                itemView.message_time.setTextColor(itemView.context.getColor(R.color.gray))
                itemView.count_message_unread.visibility = View.GONE
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

class UserDiffCallBack:DiffUtil.ItemCallback<UserTest>(){
    override fun areItemsTheSame(oldItem: UserTest, newItem: UserTest): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: UserTest, newItem: UserTest): Boolean {
        return oldItem == newItem
    }

}