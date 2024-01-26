package com.android.hoang.chatapplication.domain.repository

import com.android.hoang.chatapplication.data.remote.model.UserFirebase

interface FriendRepository {
    suspend fun addFriend(user: UserFirebase): String
    suspend fun getListSentFriend(): MutableList<String>
    suspend fun getListRequestFriend(): MutableList<String>
    suspend fun getListFriend(): MutableList<String>
    suspend fun checkExistFriend(userId: String): String
}