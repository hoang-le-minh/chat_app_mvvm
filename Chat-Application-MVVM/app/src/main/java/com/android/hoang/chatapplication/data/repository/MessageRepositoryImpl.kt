package com.android.hoang.chatapplication.data.repository

import com.android.hoang.chatapplication.R
import com.android.hoang.chatapplication.domain.repository.MessageRepository
import com.android.hoang.chatapplication.util.Constants.MESSAGE_TYPE_STRING
import com.blankj.utilcode.util.StringUtils
import com.google.firebase.database.FirebaseDatabase
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MessageRepositoryImpl @Inject constructor(): MessageRepository {
    override suspend fun sendMessage(
        senderId: String,
        receiverId: String,
        message: String,
        type: Int
    ): String = suspendCoroutine { continuation ->

        val myRef = FirebaseDatabase.getInstance().getReference("messages")
        var map = HashMap<String, String>()
        map["senderId"] = senderId
        map["receiverId"] = receiverId
        map["type"] = type.toString()

        if (type == MESSAGE_TYPE_STRING){
            map["message"] = message
            myRef.push().setValue(map)
                .addOnCompleteListener {
                    continuation.resume(StringUtils.getString(R.string.send_message_success))
                }
                .addOnFailureListener {
                    continuation.resume(StringUtils.getString(R.string.send_message_failed))
                }
        }

    }

}