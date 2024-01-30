package com.android.hoang.chatapplication.ui.chat

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
import com.android.hoang.chatapplication.util.Constants.LOG_TAG

class StickerAdapter : ListAdapter<Int, StickerAdapter.MyViewHolder>(
    AsyncDifferConfig.Builder(StickerDiffCallBack()).build()) {

    inner class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private val stickerItem: ImageView = itemView.findViewById(R.id.sticker_item)
        fun onBind(resource: Int){

            stickerItem.setImageResource(resource)
            stickerItem.setOnClickListener {
                Log.d(LOG_TAG, "onBind: send $resource")
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.custom_sticker_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }

}

class StickerDiffCallBack: DiffUtil.ItemCallback<Int>(){
    override fun areItemsTheSame(oldItem: Int, newItem: Int): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean {
        return oldItem == newItem
    }

}