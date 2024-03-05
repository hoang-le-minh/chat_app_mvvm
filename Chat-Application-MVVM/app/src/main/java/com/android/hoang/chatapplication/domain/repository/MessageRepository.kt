package com.android.hoang.chatapplication.domain.repository

import com.android.hoang.chatapplication.data.remote.model.Message

interface MessageRepository {
    suspend fun sendMessage(senderId: String, receiverId: String, message: String, type: Int): String
    suspend fun readMessage(senderId: String, receiverId: String): MutableList<Message>
    suspend fun checkConversationCreated(senderId: String, receiverId: String): String
    suspend fun latestMessage(senderId: String, receiverId: String): Message?
    suspend fun latestMessageList(listId: List<String>): MutableList<Message>
    suspend fun conversationList(): MutableList<String>
    suspend fun deleteConversation(userId: String): String
}