package com.android.hoang.chatapplication.ui.main.friend.friendviewpager.alluser

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.android.hoang.chatapplication.R
import com.android.hoang.chatapplication.data.remote.model.Friend
import com.android.hoang.chatapplication.data.remote.model.UserFirebase
import com.android.hoang.chatapplication.ui.chat.ChatActivity
import com.android.hoang.chatapplication.util.Constants.LOG_TAG
import com.android.hoang.chatapplication.util.Status
import com.blankj.utilcode.util.StringUtils
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.coroutines.resume

class AllUserAdapter(private val users: List<Any>, private val allUserViewModel: AllUserFragmentViewModel) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
        private val layoutInfo: RelativeLayout = itemView.findViewById(R.id.layout_info)
        private val btnAddFriend: Button = itemView.findViewById(R.id.btn_add_friend)

        fun bind(user: UserFirebase) {
            Glide.with(itemView.context).load(user.imageUrl).error(R.drawable.avt_default).into(userAvt)
            username.text = user.username
            layoutInfo.setOnClickListener {
                val intent = Intent(itemView.context, ChatActivity::class.java)
                intent.putExtra("user_id", user.id)
                Log.d(LOG_TAG, "bind: $user")
                itemView.context.startActivity(intent)
            }

            updateStatusButtonAddFriend(user.id, btnAddFriend)

            btnAddFriend.setOnClickListener {
                btnAddFriend.visibility = View.INVISIBLE
                allUserViewModel.addFriend(user)
            }
        }
    }

    inner class SectionHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val sectionHeaderTextView: TextView = itemView.findViewById(R.id.contact_section)

        fun bind(sectionHeader: String) {
            sectionHeaderTextView.text = sectionHeader
        }
    }

    private fun updateStatusButtonAddFriend(userId: String, btnAdd: Button){
        val currentUser = FirebaseAuth.getInstance().currentUser
        val addRef = FirebaseDatabase.getInstance().getReference("friends").child(currentUser?.uid + userId)
        val requestRef = FirebaseDatabase.getInstance().getReference("friends").child(userId + currentUser?.uid)
        if (currentUser != null){
            addRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val friend = snapshot.getValue(Friend::class.java)
                    if (friend == null || friend.id_user_1 == "" || friend.id_user_2 == "") {
                        requestRef.addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val friend1 = snapshot.getValue(Friend::class.java)
                                if (friend1 == null || friend1.id_user_1 == "" || friend1.id_user_2 == "") {
                                    btnAdd.visibility = View.VISIBLE
                                    requestRef.removeEventListener(this)
                                }
                                else {
                                    btnAdd.visibility = View.INVISIBLE
                                    requestRef.removeEventListener(this)
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {

                            }

                        })
                        addRef.removeEventListener(this)
                    }
                    else {
                        btnAdd.visibility = View.INVISIBLE
                        addRef.removeEventListener(this)
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

        }
    }
}