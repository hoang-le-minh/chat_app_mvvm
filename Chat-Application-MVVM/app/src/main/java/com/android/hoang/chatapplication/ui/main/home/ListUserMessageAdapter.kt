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
import com.android.hoang.chatapplication.data.remote.model.UserFirebase
import com.android.hoang.chatapplication.ui.chat.ChatActivity
import com.android.hoang.chatapplication.util.Constants
import com.android.hoang.chatapplication.util.Constants.LOG_TAG
import com.android.hoang.chatapplication.util.Constants.MESSAGE_TYPE_STRING
import com.blankj.utilcode.util.StringUtils
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class ListUserMessageAdapter : ListAdapter<Message, ListUserMessageAdapter.MyViewHolder>(
    AsyncDifferConfig.Builder(UserDiffCallBack()).build()) {


    inner class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private var userAvt = itemView.findViewById<ImageView>(R.id.user_avt)
        private var userName = itemView.findViewById<TextView>(R.id.user_name)
        private var messageTime = itemView.findViewById<TextView>(R.id.message_time)
        private var latestMessage = itemView.findViewById<TextView>(R.id.latest_message)
        private var countMessageUnread = itemView.findViewById<TextView>(R.id.count_message_unread)
        fun onBind(message: Message){
            val currentUser = FirebaseAuth.getInstance().currentUser
            val userId = if (message.senderId == currentUser?.uid) message.receiverId else message.senderId
            messageTime.text = formatDateStr(message.createAt)
            val pre = if (message.senderId == currentUser?.uid) StringUtils.getString(R.string.you) else ""
            latestMessage.text = if (message.type == MESSAGE_TYPE_STRING.toString()){
                if (message.message.length > 50) "$pre ${message.message.substring(0, 48)}..." else "$pre ${message.message}"
            } else {
                "$pre ${StringUtils.getString(R.string.sent_an_image)}"
            }

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, ChatActivity::class.java)
                intent.putExtra("user_id", userId)
                Log.d(LOG_TAG, "homeAdapter.bind: $userId")
                itemView.context.startActivity(intent)
            }

            setInfoUser(itemView, userId, userAvt, userName)
            unreadMessageCount(currentUser?.uid!!, userId, object : UnreadMessageCountCallback{
                override fun onUnreadMessageCountLoaded(count: Int) {
                    if(count > 0){
                        latestMessage.setTextColor(itemView.context.getColor(R.color.black))
                        messageTime.setTextColor(itemView.context.getColor(R.color.black))
                        countMessageUnread.text = count.toString()
                        countMessageUnread.visibility = View.VISIBLE
                    } else {
                        latestMessage.setTextColor(itemView.context.getColor(R.color.gray))
                        messageTime.setTextColor(itemView.context.getColor(R.color.gray))
                        countMessageUnread.visibility = View.GONE
                    }
                }

            })


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.custom_message_item_view, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }

    fun formatDateStr(dateString: String): String{
        // format date
        val sdf = SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault())
        val createAtDate = sdf.parse(dateString)

        val currentDate = Calendar.getInstance()
        currentDate.set(Calendar.HOUR_OF_DAY, 0)
        currentDate.set(Calendar.MINUTE, 0)
        currentDate.set(Calendar.SECOND, 0)
        currentDate.set(Calendar.MILLISECOND, 0)

        return if (createAtDate?.before(currentDate.time) == false){
            dateString.split(" ").first()
        } else {
            dateString
        }
    }

    private fun unreadMessageCount(currentId: String, userId: String, callback: UnreadMessageCountCallback){
        var unreadCount: Int
        val messageRef = FirebaseDatabase.getInstance().getReference("messages")
        messageRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                unreadCount = 0
                for (dataSnapshot: DataSnapshot in snapshot.children){
                    val message = dataSnapshot.getValue(Message::class.java)
                    val isSeen = dataSnapshot.child("isSeen").value.toString()
                    if (message?.receiverId == currentId && message.senderId == userId && isSeen == "false"){
                        unreadCount++
                    }
                }
                
                callback.onUnreadMessageCountLoaded(unreadCount)
                Log.d(LOG_TAG, "unreadMessageCount.onDataChange: $unreadCount")

            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(LOG_TAG, "unreadMessageCount.onCancelled: ${error.message}")
            }

        })
    }

    private fun setInfoUser(itemView: View, userId: String, img_avt: ImageView, txt_name: TextView) {
        val myRef = FirebaseDatabase.getInstance().getReference("users").child(userId)
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(UserFirebase::class.java)
                Log.d(LOG_TAG, "setInfoUser.onDataChange: ${user.toString()}")
                val errorUri = if (user?.imageUrl != "") R.drawable.no_image else R.drawable.avt_default
                Glide.with(itemView.context).load(user?.imageUrl).error(errorUri).into(img_avt)
                txt_name.text = user?.username

            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(LOG_TAG, "setInfoUser.onCancelled: ${error.message}")

            }

        })
    }


}

interface UnreadMessageCountCallback {
    fun onUnreadMessageCountLoaded(count: Int)
}

class UserDiffCallBack: DiffUtil.ItemCallback<Message>(){
    override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem.createAt == newItem.createAt
    }

    override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem == newItem
    }

}