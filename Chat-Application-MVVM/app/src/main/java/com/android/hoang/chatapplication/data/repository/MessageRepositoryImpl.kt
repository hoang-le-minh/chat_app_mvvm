package com.android.hoang.chatapplication.data.repository

import android.net.Uri
import android.util.Log
import com.android.hoang.chatapplication.R
import com.android.hoang.chatapplication.data.remote.model.Conversation
import com.android.hoang.chatapplication.data.remote.model.Message
import com.android.hoang.chatapplication.data.remote.model.UserFirebase
import com.android.hoang.chatapplication.domain.repository.MessageRepository
import com.android.hoang.chatapplication.util.Constants
import com.android.hoang.chatapplication.util.Constants.LOG_TAG
import com.android.hoang.chatapplication.util.Constants.MESSAGE_TYPE_IMAGE
import com.android.hoang.chatapplication.util.Constants.MESSAGE_TYPE_STRING
import com.blankj.utilcode.util.StringUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MessageRepositoryImpl @Inject constructor(): MessageRepository {
    override suspend fun sendMessage(
        senderId: String,
        receiverId: String,
        message: String,
        type: Int
    ): String = suspendCoroutine { continuation ->

        val sdf = SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault())
        val timestamp = sdf.format(Date())
        val myRef = FirebaseDatabase.getInstance().getReference("messages")
        val map = HashMap<String, Any>()
        map["senderId"] = senderId
        map["receiverId"] = receiverId
        map["type"] = type.toString()
        map["isSeen"] = "false"
        map["createAt"] = timestamp

        if (type == MESSAGE_TYPE_IMAGE){
            val storageRef = FirebaseStorage.getInstance().reference.child("imageMessage/"+UUID.randomUUID())
            storageRef.putFile(Uri.fromFile(File(message)))
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val url = task.result.toString()
                            // Now, save the imageUrl to Firebase Realtime Database
                            map["message"] = url
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
                .addOnFailureListener{
                    Log.d(LOG_TAG, "send_image_error")
                    continuation.resume(StringUtils.getString(R.string.something_went_wrong))
                }
        } else {
            // send message
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

    override suspend fun readMessage(senderId: String, receiverId: String): MutableList<Message> = suspendCoroutine{ continuation ->
        val messageList = mutableListOf<Message>()
        val currentUser = FirebaseAuth.getInstance().currentUser
        val myRef = FirebaseDatabase.getInstance().getReference("messages")
        if(currentUser != null){
            var isResumed = false
            myRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()
                    for (dataSnapshot: DataSnapshot in snapshot.children){
                        val message = dataSnapshot.getValue(Message::class.java) ?: return
                        val isSeen = dataSnapshot.child("isSeen").value.toString()
                        if((message.senderId == senderId && message.receiverId == receiverId) || (message.receiverId == senderId && message.senderId == receiverId) ){
                            message.isSeen = isSeen
                            messageList.add(message)
                        }
                    }
                    if (!isResumed){
                        continuation.resume(messageList)
                        isResumed = true
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d(LOG_TAG, "onCancelled: ${error.message}")
                }

            })
        }

    }

    override suspend fun checkConversationCreated(senderId: String, receiverId: String): String = suspendCoroutine { continuation ->
        // create conversation if doesn't exist
        val conversationRef = FirebaseDatabase.getInstance().getReference("conversations")
        val convMap = HashMap<String, String>()
        convMap["latestOwner"] = senderId

        conversationRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var count = 0
                outerLoop@ for (dataSnapshot: DataSnapshot in snapshot.children){
                    val conversation = dataSnapshot.getValue(Conversation::class.java)
                    if ((conversation?.senderId == senderId && conversation.receiverId == receiverId) || (conversation?.senderId == receiverId && conversation.receiverId == senderId)){
                        count += 1
                        break@outerLoop
                    }
                }
                if(count == 0){
                    convMap["senderId"] = senderId
                    convMap["receiverId"] = receiverId
                    conversationRef.push().setValue(convMap)
                    continuation.resume(StringUtils.getString(R.string.create_conversation))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(LOG_TAG, "onCancelled: ${error.message}")
            }

        })
    }

    override suspend fun latestMessage(senderId: String, receiverId: String): Message? = suspendCoroutine{ continuation ->
        var result: Message? = null
        val currentUser = FirebaseAuth.getInstance().currentUser
        val myRef = FirebaseDatabase.getInstance().getReference("messages")
        if(currentUser != null){
            myRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (dataSnapshot: DataSnapshot in snapshot.children.reversed()){
                        val message = dataSnapshot.getValue(Message::class.java) ?: continue
                        if((message.senderId == senderId && message.receiverId == receiverId) || (message.receiverId == senderId && message.senderId == receiverId) ){
                            result = message
                            Log.d(LOG_TAG, "onDataChange latestMessage: $message")
                            break
                        }
                    }
                    continuation.resume(result)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d(LOG_TAG, "onCancelled: ${error.message}")
                }

            })
        }

    }

    override suspend fun latestMessageList(listId: List<String>): MutableList<Message> = suspendCoroutine { continuation ->
        val currentUser = FirebaseAuth.getInstance().currentUser
        val myRef = FirebaseDatabase.getInstance().getReference("messages")
        val messageList = mutableListOf<Message>()

        if (currentUser != null){
            var isResumed = false
            myRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Xóa tất cả các dữ liệu cũ trong messageList
                    messageList.clear()

                    for (userId in listId) {
                        for (dataSnapshot: DataSnapshot in snapshot.children.reversed()) {
                            val message = dataSnapshot.getValue(Message::class.java) ?: continue
                            if ((message.senderId == userId && message.receiverId == currentUser.uid) || (message.senderId == currentUser.uid && message.receiverId == userId)) {
                                messageList.add(message)
                                break
                            }
                        }
                    }

                    // Đã hoàn thành việc cập nhật messageList
                    if (!isResumed){
                        continuation.resume(messageList)
                        isResumed = true
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d(LOG_TAG, "latestMessageList.onCancelled: ${error.message}")
                }
            })
        }
    }

    override suspend fun conversationList(): MutableList<String> = suspendCoroutine{ continuation ->

        val currentUser = FirebaseAuth.getInstance().currentUser
        val myRef = FirebaseDatabase.getInstance().getReference("conversations")
        val listId = mutableListOf<String>()
        var isResumed = false
        if (currentUser != null){
            myRef.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    listId.clear()
                    for (dataSnapshot: DataSnapshot in snapshot.children.reversed()) {
                        val conversation = dataSnapshot.getValue(Conversation::class.java)
                        if (conversation?.senderId == currentUser.uid){
                            listId.add(conversation.receiverId)
                        }
                        if (conversation?.receiverId == currentUser.uid){
                            listId.add(conversation.senderId)
                        }
                    }

                    if (!isResumed){
                        continuation.resume(listId)
                        isResumed = true
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d(LOG_TAG, "conversationList.onCancelled: ${error.message}")
                }

            })
        }

    }

    override suspend fun deleteConversation(userId: String): String = suspendCoroutine{ continuation ->
        val currentUser = FirebaseAuth.getInstance().currentUser
        val messageRef = FirebaseDatabase.getInstance().getReference("messages")
        val conversationRef = FirebaseDatabase.getInstance().getReference("conversations")

        if (currentUser != null){
            var isResumedMsg = false
            var isResumedConv = false
            messageRef.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (dataSnapshot: DataSnapshot in snapshot.children) {
                        val message = dataSnapshot.getValue(Message::class.java) ?: continue
                        if ((message.senderId == currentUser.uid && message.receiverId == userId) ||
                            (message.senderId == userId && message.receiverId == currentUser.uid)){
                            dataSnapshot.ref.removeValue().addOnSuccessListener {
                                if(isResumedMsg){
                                    continuation.resume(StringUtils.getString(R.string.ok))
                                    isResumedMsg = true
                                }
                            }
                                .addOnFailureListener {
                                    if (isResumedMsg){
                                        continuation.resume(StringUtils.getString(R.string.something_went_wrong))
                                        isResumedMsg = true
                                    }
                                }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    continuation.resume(StringUtils.getString(R.string.something_went_wrong))
                }

            })

            conversationRef.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (dataSnapshot: DataSnapshot in snapshot.children) {
                        val conversation = dataSnapshot.getValue(Conversation::class.java) ?: continue
                        if ((conversation.senderId == currentUser.uid && conversation.receiverId == userId) ||
                                conversation.senderId == userId && conversation.receiverId == currentUser.uid){
                            dataSnapshot.ref.removeValue().addOnSuccessListener {
                                continuation.resume(StringUtils.getString(R.string.ok))
                            }.addOnFailureListener {
                                continuation.resume(StringUtils.getString(R.string.something_went_wrong))
                            }
                            break
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    continuation.resume(StringUtils.getString(R.string.something_went_wrong))

                }

            })
        }
    }

}