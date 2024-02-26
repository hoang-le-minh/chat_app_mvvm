package com.android.hoang.chatapplication.ui.main.home

import android.content.Intent
import android.util.Log
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
import com.android.hoang.chatapplication.data.remote.model.Message
import com.android.hoang.chatapplication.data.remote.model.SearchMessage
import com.android.hoang.chatapplication.data.remote.model.UserFirebase
import com.android.hoang.chatapplication.ui.chat.ChatActivity
import com.android.hoang.chatapplication.util.Constants
import com.blankj.utilcode.util.StringUtils
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*

class SearchMessageAdapter : ListAdapter<SearchMessage, SearchMessageAdapter.MyViewHolder>(
    AsyncDifferConfig.Builder(SearchMessageDiffCallBack()).build()) {


    inner class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private var userAvt = itemView.findViewById<ImageView>(R.id.user_avt)
        private var userName = itemView.findViewById<TextView>(R.id.user_name)
        private var messageTime = itemView.findViewById<TextView>(R.id.message_time)
        private var latestMessage = itemView.findViewById<TextView>(R.id.latest_message)
        private var countMessageUnread = itemView.findViewById<TextView>(R.id.count_message_unread)
        fun onBind(searchMessage: SearchMessage){
            val currentUser = FirebaseAuth.getInstance().currentUser
            val userId = searchMessage.partnerId
            messageTime.visibility = View.GONE

            val textMessage = "${searchMessage.countMatching} ${StringUtils.getString(R.string.matching_message)}"
            latestMessage.text = textMessage

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, ChatActivity::class.java)
                intent.putExtra("user_id", userId)
                Log.d(Constants.LOG_TAG, "searchMessageAdapter.bind: $userId")
                itemView.context.startActivity(intent)
            }

            setInfoUser(itemView, userId, userAvt, userName)
            countMessageUnread.visibility = View.GONE

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.custom_message_item_view, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }



    private fun setInfoUser(itemView: View, userId: String, img_avt: ImageView, txt_name: TextView) {
        val myRef = FirebaseDatabase.getInstance().getReference("users").child(userId)
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(UserFirebase::class.java)
                Log.d(Constants.LOG_TAG, "setInfoUser.onDataChange: ${user.toString()}")
                val errorUri = if (user?.imageUrl != "") R.drawable.no_image else R.drawable.avt_default
                Glide.with(itemView.context).load(user?.imageUrl).error(errorUri).into(img_avt)
                txt_name.text = user?.username

            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(Constants.LOG_TAG, "setInfoUser.onCancelled: ${error.message}")

            }

        })
    }


}


class SearchMessageDiffCallBack: DiffUtil.ItemCallback<SearchMessage>(){
    override fun areItemsTheSame(oldItem: SearchMessage, newItem: SearchMessage): Boolean {
        return oldItem.partnerId == newItem.partnerId
    }

    override fun areContentsTheSame(oldItem: SearchMessage, newItem: SearchMessage): Boolean {
        return oldItem == newItem
    }

}