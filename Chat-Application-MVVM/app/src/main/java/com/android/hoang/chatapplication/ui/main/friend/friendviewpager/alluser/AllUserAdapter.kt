package com.android.hoang.chatapplication.ui.main.friend.friendviewpager.alluser

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.hoang.chatapplication.R
import com.android.hoang.chatapplication.data.remote.model.UserFirebase
import com.bumptech.glide.Glide

class AllUserAdapter(private val users: List<Any>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
                val user = users[position] as UserFirebase
                holder.bind(user)
            }
            is SectionHeaderViewHolder -> {
                val sectionHeader = users[position] as String
                holder.bind(sectionHeader)
            }
        }
    }

    override fun getItemCount(): Int {
        return users.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (users[position] is String) {
            VIEW_TYPE_SECTION_HEADER
        } else {
            VIEW_TYPE_USER
        }
    }

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userAvt: ImageView = itemView.findViewById(R.id.user_avt)
        private val username: TextView = itemView.findViewById(R.id.user_name)

        fun bind(user: UserFirebase) {
            Glide.with(itemView.context).load(user.imageUrl).error(R.drawable.avt_default).into(userAvt)
            username.text = user.username
        }
    }

    inner class SectionHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val sectionHeaderTextView: TextView = itemView.findViewById(R.id.contact_section)

        fun bind(sectionHeader: String) {
            sectionHeaderTextView.text = sectionHeader
        }
    }
}