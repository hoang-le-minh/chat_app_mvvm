package com.android.hoang.chatapplication.ui.main.friend.friendviewpager.friendrequest

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.hoang.chatapplication.R
import com.android.hoang.chatapplication.data.remote.model.UserFirebase
import com.android.hoang.chatapplication.ui.chat.ChatActivity
import com.android.hoang.chatapplication.ui.main.MainActivityViewModel
import com.android.hoang.chatapplication.util.Constants
import com.bumptech.glide.Glide
import java.lang.ref.WeakReference

class FriendRequestAdapter(private val mainViewModel: MainActivityViewModel, private val friendRequestViewModel: FriendRequestFragmentViewModel, private val request: Boolean) : ListAdapter<UserFirebase, FriendRequestAdapter.MyViewHolder>(
    AsyncDifferConfig.Builder(UserDiffCallBack()).build()) {

//    interface OnButtonClickListener {
//        fun onButtonClick(position: Int)
//    }
//
//    private lateinit var mListener: OnButtonClickListener
//    fun setOnButtonClickListener(listener: OnButtonClickListener) {
//        mListener = listener
//    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userAvt: ImageView = itemView.findViewById(R.id.user_avt)
        private val username: TextView = itemView.findViewById(R.id.user_name)
        private val layoutInfo: RelativeLayout = itemView.findViewById(R.id.layout_info)
        private val btnAddFriend: Button = itemView.findViewById(R.id.btn_add_friend)
        private val btnCancel: Button = itemView.findViewById(R.id.btn_cancel_friend)
        private val btnAccept: Button = itemView.findViewById(R.id.btn_accept_friend)
        private val txtRefuse: TextView = itemView.findViewById(R.id.txt_refuse)
        private val view = WeakReference(itemView)

        init {
            view.get()?.let {
                it.setOnClickListener {
                    if (view.get()?.scrollX != 0){
                        view.get()?.scrollTo(0, 0)
                    }
                }
            }
        }

        fun onBind(user: UserFirebase) {

            Glide.with(itemView.context).load(user.imageUrl).error(R.drawable.avt_default)
                .into(userAvt)
            username.text = user.username

            if (request) {
                btnAddFriend.visibility = View.INVISIBLE
                btnAccept.visibility = View.VISIBLE
                btnCancel.visibility = View.INVISIBLE
            } else {
                btnAddFriend.visibility = View.INVISIBLE
                btnAccept.visibility = View.INVISIBLE
                btnCancel.visibility = View.VISIBLE
            }

            btnAccept.setOnClickListener {
//                mListener.onButtonClick(adapterPosition)
                friendRequestViewModel.acceptFriend(user.id)
                friendRequestViewModel.getRequestIdList()
                btnAccept.visibility = View.INVISIBLE
                mainViewModel.updateListFriendStatus(true)
                mainViewModel.updateRequestCount(true)
            }

            btnCancel.setOnClickListener {
//                mListener.onButtonClick(adapterPosition)
                friendRequestViewModel.cancelFriendRequest(user.id)
                friendRequestViewModel.getSentIdList()
                btnCancel.visibility = View.INVISIBLE
                mainViewModel.updateAllUserStatus(true)
            }

            layoutInfo.setOnClickListener {
                val intent = Intent(itemView.context, ChatActivity::class.java)
                intent.putExtra("user_id", user.id)
                Log.d(Constants.LOG_TAG, "friendRequestAdapter.bind: ${user.id}")
                itemView.context.startActivity(intent)
            }

            txtRefuse.setOnClickListener {
                friendRequestViewModel.refuseFriend(user)
                friendRequestViewModel.getRequestIdList()
                btnAccept.visibility = View.INVISIBLE
                mainViewModel.updateRequestCount(true)
                mainViewModel.updateAllUserStatus(true)
            }

        }

        fun updateView(){
            view.get()?.scrollTo(0, 0)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.custom_user_request_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.onBind(getItem(position))
        holder.updateView()
    }
}

class UserDiffCallBack: DiffUtil.ItemCallback<UserFirebase>(){
    override fun areItemsTheSame(oldItem: UserFirebase, newItem: UserFirebase): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: UserFirebase, newItem: UserFirebase): Boolean {
        return oldItem == newItem
    }

}