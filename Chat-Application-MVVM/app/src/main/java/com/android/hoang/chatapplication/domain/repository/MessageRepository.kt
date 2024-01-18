package com.android.hoang.chatapplication.domain.repository

interface MessageRepository {
    suspend fun sendMessage(senderId: String, receiverId: String, message: String, type: Int): String
}