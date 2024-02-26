package com.android.hoang.chatapplication.ui.main.friend

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

class SearchUserAdapter(private val users: List<UserFirebase>) : RecyclerView.Adapter<SearchUserAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.custom_user_item, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchUserAdapter.UserViewHolder, position: Int) {
        val user = users[position]
        holder.bind(user)
    }

    override fun getItemCount(): Int {
        return users.size
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


}