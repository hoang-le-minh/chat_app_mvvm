package com.android.hoang.chatapplication.data.remote.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Message(var senderId: String = "", var receiverId: String = "", var message: String = "", var type: String = "", var createAt: String = "", var isSeen: String = ""):
    Parcelable {
}