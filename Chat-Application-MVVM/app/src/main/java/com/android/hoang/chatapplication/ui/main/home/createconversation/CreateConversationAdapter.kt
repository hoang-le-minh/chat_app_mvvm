package com.android.hoang.chatapplication.ui.main.home.createconversation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.hoang.chatapplication.R
import com.android.hoang.chatapplication.data.remote.model.UserFirebase
import com.bumptech.glide.Glide
import java.util.*

class CreateConversationAdapter(private val createConversationViewModel: CreateConversationFragmentViewModel) : ListAdapter<UserFirebase, CreateConversationAdapter.MyViewHolder>(
    AsyncDifferConfig.Builder(UserDiffCallBack()).build()) {

    private var selectedItem = -1

    inner class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private val userAvt: ImageView = itemView.findViewById(R.id.user_avt)
        private val txtUsername: TextView = itemView.findViewById(R.id.user_name)
        val checkBox: CheckBox = itemView.findViewById(R.id.checkbox_create)

        fun onBind(user: UserFirebase){
            if (selectedItem == -1){
                checkBox.isChecked = false
            } else {
                checkBox.isChecked = selectedItem == adapterPosition
            }
            itemView.setOnClickListener {
                if (selectedItem != adapterPosition){
                    notifyItemChanged(selectedItem)
                    selectedItem = adapterPosition
                    checkBox.isChecked = true
                    createConversationViewModel.setUserSelected(user)
                }
            }

            checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    if (selectedItem != adapterPosition){
                        notifyItemChanged(selectedItem)
                        selectedItem = adapterPosition
                    }
                    createConversationViewModel.setUserSelected(user)
                } else{
                    if (selectedItem == adapterPosition){
                        createConversationViewModel.setUserSelected(null)
                    }
                }
            }

            Glide.with(itemView.context).load(user.imageUrl).error(R.drawable.avt_default).into(userAvt)
            txtUsername.text = user.username

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.custom_create_conversation_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.onBind(getItem(position))
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