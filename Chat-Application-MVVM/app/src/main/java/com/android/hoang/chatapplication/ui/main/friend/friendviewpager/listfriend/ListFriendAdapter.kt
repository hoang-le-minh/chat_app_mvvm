package com.android.hoang.chatapplication.ui.main.friend.friendviewpager.listfriend

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.hoang.chatapplication.R
import com.android.hoang.chatapplication.data.remote.model.UserFirebase
import com.android.hoang.chatapplication.ui.chat.ChatActivity
import com.android.hoang.chatapplication.util.Constants
import com.bumptech.glide.Glide

class ListFriendAdapter(private val friends: List<Any>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val VIEW_TYPE_USER = 1
    private val VIEW_TYPE_SECTION_HEADER = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_USER -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.custom_user_item, parent, false)
                UserViewHolder(view)
            }
            VIEW_TYPE_SECTION_HEADER -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.all_user_section_header, parent, false)
                SectionHeaderViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is UserViewHolder -> {
                val user = friends[position] as UserFirebase
                holder.bind(user)
            }
            is SectionHeaderViewHolder -> {
                val sectionHeader = friends[position] as String
                holder.bind(sectionHeader)
            }
        }
    }

    override fun getItemCount(): Int {
        return friends.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (friends[position] is String) {
            VIEW_TYPE_SECTION_HEADER
        } else {
            VIEW_TYPE_USER
        }
    }

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userAvt: ImageView = itemView.findViewById(R.id.user_avt)
        private val username: TextView = itemView.findViewById(R.id.user_name)
        private val layoutInfo: RelativeLayout = itemView.findViewById(R.id.layout_info)
        private val btnAddFriend: Button = itemView.findViewById(R.id.btn_add_friend)

        fun bind(user: UserFirebase) {
            Glide.with(itemView.context).load(user.imageUrl).error(R.drawable.avt_default).into(userAvt)
            username.text = user.username
            layoutInfo.setOnClickListener {
                val intent = Intent(itemView.context, ChatActivity::class.java)
                intent.putExtra("user_id", user.id)
                Log.d(Constants.LOG_TAG, "bind: $user")
                itemView.context.startActivity(intent)
            }

            btnAddFriend.visibility = View.INVISIBLE
        }
    }

    inner class SectionHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val sectionHeaderTextView: TextView = itemView.findViewById(R.id.contact_section)

        fun bind(sectionHeader: String) {
            sectionHeaderTextView.text = sectionHeader
        }
    }

}